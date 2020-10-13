package org.fromlabs.demo.ordermanagement.core.messaging

import io.micronaut.configuration.kafka.exceptions.DefaultKafkaListenerExceptionHandler
import io.micronaut.configuration.kafka.exceptions.KafkaListenerException
import io.micronaut.configuration.kafka.exceptions.KafkaListenerExceptionHandler
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.SerializationException
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

private class FailedRecord(
    private val maxAttemps: Int,
    private val retryDelay: Long,
    val offset: Long
) {
    private val attempsCount = AtomicInteger(1)

    fun nextSleep(): Long {
        return if (attempsCount.incrementAndGet() <= maxAttemps) retryDelay else 0
    }
}

@Factory
class SeekToCurrentListenerExceptionHandlerFactory {
    @Singleton
    @Replaces(DefaultKafkaListenerExceptionHandler::class)
    fun kafkaListenerExceptionHandler(): KafkaListenerExceptionHandler {
        return SeekToCurrentListenerExceptionHandler(100, 100)
    }
}

class SeekToCurrentListenerExceptionHandler(private val maxAttemps: Int, private val retryDelay: Long) :
    KafkaListenerExceptionHandler {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(SeekToCurrentListenerExceptionHandler::class.java)
    }

    private val failedRecords = ThreadLocal<MutableMap<TopicPartition, FailedRecord>>()
    private val defaultKafkaListenerExceptionHandler: KafkaListenerExceptionHandler =
        DefaultKafkaListenerExceptionHandler()

    override fun handle(exception: KafkaListenerException) {
        val cause = exception.cause

        if (cause is SerializationException) {
            defaultKafkaListenerExceptionHandler.handle(exception)
        } else if (cause is Exception) {
            if (isRetryableException(cause)
                && retry(exception.consumerRecord.get(), exception.kafkaConsumer)
            ) {
                // skip
                return
            }
            recover(exception.consumerRecord.get())
        }
    }

    private fun isRetryableException(exception: Exception): Boolean {
        // FIXME rtassi: isRetryableException()
        return true
    }

    private fun retry(consumerRecord: ConsumerRecord<*, *>, kafkaConsumer: Consumer<*, *>): Boolean {
        if (maxAttemps > 1) {
            var consumerFailedRecords = failedRecords.get()
            if (consumerFailedRecords == null) {
                consumerFailedRecords = HashMap()
                failedRecords.set(consumerFailedRecords)
            }
            val topicPartition = TopicPartition(consumerRecord.topic(), consumerRecord.partition())
            var failedRecord = consumerFailedRecords[topicPartition]
            if (failedRecord == null || failedRecord.offset != consumerRecord.offset()) {
                failedRecord = FailedRecord(maxAttemps, retryDelay, consumerRecord.offset())
                consumerFailedRecords[topicPartition] = failedRecord
            }
            val nextSleep = failedRecord.nextSleep()
            return if (nextSleep > 0) {
                kafkaConsumer.seek(topicPartition, consumerRecord.offset())
                try {
                    Thread.sleep(nextSleep)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                true
            } else {
                false
            }
        }
        return false
    }

    private fun recover(consumerRecord: ConsumerRecord<*, *>) {
        logger.info("Recover failed record ${consumerRecord.key()}")

        val consumerFailedRecords = failedRecords.get()
        consumerFailedRecords?.remove(TopicPartition(consumerRecord.topic(), consumerRecord.partition()))
    }
}
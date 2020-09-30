package org.fromlabs.demo.ordermanagement.core.messaging

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.context.annotation.Property
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.scheduling.annotation.Async
import io.micronaut.transaction.annotation.TransactionalEventListener
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import javax.inject.Singleton

data class MessagesCommittedEvent(val messages: Sequence<MessageData>);

@Singleton
open class KafkaMessageProducer(
    private val applicationEventPublisher: ApplicationEventPublisher,
    @KafkaClient(
        "message-client",
        acks = KafkaClient.Acknowledge.ALL,
        properties = [
            Property(name = "max.in.flight.requests.per.connection", value = "1")
        ]
    ) private val kafkaProducer: Producer<String, String>
) : MessageProducer {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(KafkaMessageProducer::class.java)
    }

    override fun send(messages: Sequence<MessageData>) {
        applicationEventPublisher.publishEvent(MessagesCommittedEvent(messages))
    }

    @TransactionalEventListener
    open fun onCommitedMessages(event: MessagesCommittedEvent) {
        sendAsync(event.messages)
    }

    @Async
    open fun sendAsync(messages: Sequence<MessageData>) {
        messages.forEach<MessageData>(this@KafkaMessageProducer::send)
    }

    private fun send(message: MessageData) {
        logger.info("Send message $message")

        kafkaProducer.send(
            ProducerRecord<String, String>(
                message.topic,
                null,
                message.key,
                message.payload,
                message.headers.map { RecordHeader(it.name, it.value.toByteArray()) }
            )
        )
    }
}
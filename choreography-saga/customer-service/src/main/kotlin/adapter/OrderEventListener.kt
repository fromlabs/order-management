package org.fromlabs.demo.ordermanagement.customer.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.fromlabs.demo.ordermanagement.core.domain.DomainEventEnvelope
import org.fromlabs.demo.ordermanagement.customer.application.port.CreateOrderSaga
import org.fromlabs.demo.ordermanagement.customer.application.port.OrderCreatedEventDto
import java.nio.charset.Charset

@KafkaListener(groupId = "customer-client", threads = 3)
class OrderEventListener(
    private val createOrderSaga: CreateOrderSaga,
    private val objectMapper: ObjectMapper
) {

    @Topic("order.order.events")
    fun onConsumerRecord(record: ConsumerRecord<String, String>) {
        val eventType = record.headers().lastHeader("x-event-type").value().toString(Charset.forName("UTF8"))
        val payload = record.value()

        when (eventType) {
            "order-created" -> {
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderCreatedEventDto>>(payload)

                createOrderSaga.onOrderCreatedEvent(envelope.aggregateId, envelope.event)
            }
        }
    }
}
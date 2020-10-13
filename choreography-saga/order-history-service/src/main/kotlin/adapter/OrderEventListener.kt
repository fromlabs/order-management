package org.fromlabs.demo.ordermanagement.orderhistory.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.fromlabs.demo.ordermanagement.core.domain.DomainEventEnvelope
import org.fromlabs.demo.ordermanagement.orderhistory.application.port.*
import java.nio.charset.Charset

@KafkaListener(groupId = "order-history-client", threads = 3)
class OrderEventListener(
    private val customerOrderHistoryService: CustomerOrderHistoryService,
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

                customerOrderHistoryService.onOrderCreatedEvent(envelope.aggregateId, envelope.event)
            }
            "order-approved" -> {
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderApprovedEventDto>>(payload)

                customerOrderHistoryService.onOrderApprovedEvent(envelope.aggregateId, envelope.event)
            }
            "order-rejected" -> {
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderRejectedEventDto>>(payload)

                customerOrderHistoryService.onOrderRejectedEvent(envelope.aggregateId, envelope.event)
            }
            "order-cancelled" -> {
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderCancelledEventDto>>(payload)

                customerOrderHistoryService.onOrderCancelledEvent(envelope.aggregateId, envelope.event)
            }
        }
    }
}
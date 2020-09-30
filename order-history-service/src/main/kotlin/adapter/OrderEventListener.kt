package org.fromlabs.demo.ordermanagement.orderhistory.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import domain.DomainEventEnvelope
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
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
                val javaType = TypeFactory.defaultInstance()
                    .constructParametricType(
                        DomainEventEnvelope::class.java,
                        Long::class.java,
                        OrderCreatedEventDto::class.java
                    )

                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderCreatedEventDto>>(payload, javaType)

                customerOrderHistoryService.onOrderCreatedEvent(envelope.aggregateId, envelope.event)
            }
            "order-approved" -> {
                val javaType = TypeFactory.defaultInstance()
                    .constructParametricType(
                        DomainEventEnvelope::class.java,
                        Long::class.java,
                        OrderApprovedEventDto::class.java
                    )

                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderApprovedEventDto>>(payload, javaType)

                customerOrderHistoryService.onOrderApprovedEvent(envelope.aggregateId, envelope.event)
            }
            "order-rejected" -> {
                val javaType = TypeFactory.defaultInstance()
                    .constructParametricType(
                        DomainEventEnvelope::class.java,
                        Long::class.java,
                        OrderRejectedEventDto::class.java
                    )

                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderRejectedEventDto>>(payload, javaType)

                customerOrderHistoryService.onOrderRejectedEvent(envelope.aggregateId, envelope.event)
            }
            "order-cancelled" -> {
                val javaType = TypeFactory.defaultInstance()
                    .constructParametricType(
                        DomainEventEnvelope::class.java,
                        Long::class.java,
                        OrderCancelledEventDto::class.java
                    )

                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, OrderCancelledEventDto>>(payload, javaType)

                customerOrderHistoryService.onOrderCancelledEvent(envelope.aggregateId, envelope.event)
            }
        }
    }
}
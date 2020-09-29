package org.fromlabs.demo.ordermanagement.order.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import messaging.DomainEventEnvelope
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.fromlabs.demo.ordermanagement.order.application.port.CreateOrderSaga
import org.fromlabs.demo.ordermanagement.order.application.port.CreditReservationFailedEventDto
import org.fromlabs.demo.ordermanagement.order.application.port.CreditReservedEventDto
import org.fromlabs.demo.ordermanagement.order.application.port.CustomerValidationFailedEventDto
import java.nio.charset.Charset

@KafkaListener(groupId = "order-client", threads = 3)
class CustomerEventListener(
    private val createOrderSaga: CreateOrderSaga,
    private val objectMapper: ObjectMapper
) {

    @Topic("customer.customer.events")
    fun onConsumerRecord(record: ConsumerRecord<String, String>) {
        val eventType = record.headers().lastHeader("x-event-type").value().toString(Charset.forName("UTF8"))
        val payload = record.value()

        when (eventType) {
            "credit-reserved" -> {
                val javaType = TypeFactory.defaultInstance()
                    .constructParametricType(
                        DomainEventEnvelope::class.java,
                        Long::class.java,
                        CreditReservedEventDto::class.java
                    )

                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, CreditReservedEventDto>>(payload, javaType)

                createOrderSaga.onCreditReservedEvent(envelope.aggregateId, envelope.event)
            }
            "customer-validation-failed" -> {
                val javaType = TypeFactory.defaultInstance()
                    .constructParametricType(
                        DomainEventEnvelope::class.java,
                        Long::class.java,
                        CustomerValidationFailedEventDto::class.java
                    )

                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, CustomerValidationFailedEventDto>>(
                        payload,
                        javaType
                    )

                createOrderSaga.onCustomerValidationFailedEvent(envelope.aggregateId, envelope.event)
            }
            "credit-reservation-failed" -> {
                val javaType = TypeFactory.defaultInstance()
                    .constructParametricType(
                        DomainEventEnvelope::class.java,
                        Long::class.java,
                        CreditReservationFailedEventDto::class.java
                    )

                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, CreditReservationFailedEventDto>>(
                        payload,
                        javaType
                    )

                createOrderSaga.onCreditReservationFailedEvent(envelope.aggregateId, envelope.event)
            }
        }
    }
}
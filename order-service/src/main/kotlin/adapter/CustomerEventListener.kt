package org.fromlabs.demo.ordermanagement.order.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.fromlabs.demo.ordermanagement.core.domain.DomainEventEnvelope
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
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, CreditReservedEventDto>>(payload)

                createOrderSaga.onCreditReservedEvent(envelope.aggregateId, envelope.event)
            }
            "customer-validation-failed" -> {
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, CustomerValidationFailedEventDto>>(payload)

                createOrderSaga.onCustomerValidationFailedEvent(envelope.aggregateId, envelope.event)
            }
            "credit-reservation-failed" -> {
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, CreditReservationFailedEventDto>>(payload)

                createOrderSaga.onCreditReservationFailedEvent(envelope.aggregateId, envelope.event)
            }
        }
    }
}
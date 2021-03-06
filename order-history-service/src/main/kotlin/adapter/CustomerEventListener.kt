package org.fromlabs.demo.ordermanagement.orderhistory.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.fromlabs.demo.ordermanagement.core.domain.DomainEventEnvelope
import org.fromlabs.demo.ordermanagement.orderhistory.application.port.CustomerCreatedEventDto
import org.fromlabs.demo.ordermanagement.orderhistory.application.port.CustomerOrderHistoryService
import java.nio.charset.Charset

@KafkaListener(groupId = "order-history-client", threads = 3)
class CustomerEventListener(
    private val customerOrderHistoryService: CustomerOrderHistoryService,
    private val objectMapper: ObjectMapper
) {

    @Topic("customer.customer.events")
    fun onConsumerRecord(record: ConsumerRecord<String, String>) {
        val eventType = record.headers().lastHeader("x-event-type").value().toString(Charset.forName("UTF8"))
        val payload = record.value()

        when (eventType) {
            "customer-created" -> {
                val envelope =
                    objectMapper.readValue<DomainEventEnvelope<Long, CustomerCreatedEventDto>>(payload)

                customerOrderHistoryService.onCustomerCreatedEvent(envelope.aggregateId, envelope.event)
            }
        }
    }
}
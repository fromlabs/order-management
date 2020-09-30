package org.fromlabs.demo.ordermanagement.customer.adapter

import domain.DomainEventPublisher
import org.fromlabs.demo.ordermanagement.customer.application.port.CustomerEventPublisher
import org.fromlabs.demo.ordermanagement.customer.domain.model.Customer
import javax.inject.Singleton

@Singleton
class CustomerEventPublisherImpl(
    private val domainEventPublisher: DomainEventPublisher
) : CustomerEventPublisher {
    override fun publish(customerId: Long, events: Sequence<Any>) {
        domainEventPublisher.publish(
            domain = "customer",
            aggregateType = Customer.AGGREGATE_TYPE,
            aggregateId = customerId,
            events = events
        )
    }
}


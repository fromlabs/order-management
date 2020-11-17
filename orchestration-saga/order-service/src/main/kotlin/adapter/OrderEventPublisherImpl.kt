package org.fromlabs.demo.ordermanagement.order.adapter

import org.fromlabs.demo.ordermanagement.core.domain.DomainEventPublisher
import org.fromlabs.demo.ordermanagement.order.application.port.OrderEventPublisher
import org.fromlabs.demo.ordermanagement.order.domain.model.Order
import javax.inject.Singleton

@Singleton
class OrderEventPublisherImpl(
    private val domainEventPublisher: DomainEventPublisher
) : OrderEventPublisher {
    override fun publish(order: Order, events: Sequence<Any>) {
        domainEventPublisher.publish(
            domain = "order",
            aggregateType = Order.AGGREGATE_TYPE,
            aggregateId = order.id,
            events = events
        )
    }
}
package org.fromlabs.demo.ordermanagement.order.application.port

import org.fromlabs.demo.ordermanagement.order.domain.model.Order

interface OrderEventPublisher {
    fun publish(order: Order, events: Sequence<Any>)
}
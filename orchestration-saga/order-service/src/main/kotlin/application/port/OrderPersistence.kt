package org.fromlabs.demo.ordermanagement.order.application.port

import org.fromlabs.demo.ordermanagement.order.domain.model.Order

interface OrderPersistence {
    fun findById(id: Long): Order?

    fun create(order: Order): Order

    fun update(order: Order): Order
}
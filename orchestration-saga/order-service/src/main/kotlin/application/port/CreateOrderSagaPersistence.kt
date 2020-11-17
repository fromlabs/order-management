package org.fromlabs.demo.ordermanagement.order.application.port

import org.fromlabs.demo.ordermanagement.order.domain.model.CreateOrderSaga

interface CreateOrderSagaPersistence {
    fun findById(id: Long): CreateOrderSaga?

    fun create(createOrderSaga: CreateOrderSaga): CreateOrderSaga

    fun update(createOrderSaga: CreateOrderSaga): CreateOrderSaga
}
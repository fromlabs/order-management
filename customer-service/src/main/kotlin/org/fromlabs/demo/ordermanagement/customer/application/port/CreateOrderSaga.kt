package org.fromlabs.demo.ordermanagement.customer.application.port

import org.fromlabs.demo.ordermanagement.customer.domain.model.Money

data class OrderDetailsDto(val customerId: Long, val orderTotal: Money)

data class OrderCreatedEventDto(
    val orderDetails: OrderDetailsDto
)

interface CreateOrderSaga {
    fun onOrderCreatedEvent(orderId: Long, event: OrderCreatedEventDto)
}
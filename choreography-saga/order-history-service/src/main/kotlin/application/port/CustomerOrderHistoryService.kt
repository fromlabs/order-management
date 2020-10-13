package org.fromlabs.demo.ordermanagement.orderhistory.application.port

import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.Money
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.OrderState

data class GetCustomerViewResponseDto(
    val id: Long,
    val name: String,
    val creditLimit: Money,
    val orders: Map<Long, OrderInfoDto>
)

data class OrderInfoDto(
    val orderId: Long,
    val state: OrderState,
    val orderTotal: Money
)

data class OrderDetailsDto(val customerId: Long, val orderTotal: Money)

data class CustomerCreatedEventDto(
    val name: String,
    val creditLimit: Money
)

data class OrderCreatedEventDto(
    val orderDetails: OrderDetailsDto
)

data class OrderApprovedEventDto(
    val orderDetails: OrderDetailsDto
)

data class OrderRejectedEventDto(
    val orderDetails: OrderDetailsDto
)

data class OrderCancelledEventDto(
    val orderDetails: OrderDetailsDto
)

interface CustomerOrderHistoryService {
    fun getCustomerView(id: Long): GetCustomerViewResponseDto

    fun onCustomerCreatedEvent(customerId: Long, event: CustomerCreatedEventDto)

    fun onOrderCreatedEvent(orderId: Long, event: OrderCreatedEventDto)

    fun onOrderApprovedEvent(orderId: Long, event: OrderApprovedEventDto)

    fun onOrderRejectedEvent(orderId: Long, event: OrderRejectedEventDto)

    fun onOrderCancelledEvent(orderId: Long, event: OrderCancelledEventDto)
}
package org.fromlabs.demo.ordermanagement.orderhistory.domain.model

import java.math.BigDecimal

enum class OrderState {
    PENDING, APPROVED, REJECTED, CANCELLED
}

data class Money(val amount: BigDecimal)

data class OrderInfo(
    val orderId: Long,
    val state: OrderState,
    val orderTotal: Money
)

data class CustomerView(
    val customerId: Long,
    val name: String,
    val creditLimit: Money,
    val orders: Map<Long, OrderInfo>
)

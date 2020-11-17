package org.fromlabs.demo.ordermanagement.order.domain.model

import org.fromlabs.demo.ordermanagement.core.domain.DomainAggregateWithEvents
import org.fromlabs.demo.ordermanagement.core.domain.DomainEvent
import java.math.BigDecimal

enum class OrderState {
    PENDING, APPROVED, REJECTED, CANCELLED
}

// TODO roby: utilizzare RejectionReason
enum class RejectionReason {
    INSUFFICIENT_CREDIT, UNKNOWN_CUSTOMER
}

data class Money(val amount: BigDecimal)

data class OrderDetails(val customerId: Long, val orderTotal: Money)

@DomainEvent
data class OrderCreatedEvent(
    val orderDetails: OrderDetails
)

@DomainEvent
data class OrderApprovedEvent(
    val orderDetails: OrderDetails
)

@DomainEvent
data class OrderRejectedEvent(
    val orderDetails: OrderDetails
)

@DomainEvent
data class OrderCancelledEvent(
    val orderDetails: OrderDetails
)

class PendingOrderCantBeCancelledException : Exception()

class OrderCantBeCancelledAnymoreException : Exception()

data class Order(
    val id: Long = 0,
    val version: Int = 0,
    val orderState: OrderState,
    val orderDetails: OrderDetails
) {
    companion object {
        const val AGGREGATE_TYPE: String = "order"

        @JvmStatic
        fun create(customerId: Long, orderTotal: Money): DomainAggregateWithEvents<Order, Any> {
            val orderDetails = OrderDetails(
                customerId = customerId,
                orderTotal = orderTotal
            )

            return DomainAggregateWithEvents(
                Order(
                    orderState = OrderState.PENDING,
                    orderDetails = orderDetails
                ), sequenceOf(
                    OrderCreatedEvent(
                        orderDetails = orderDetails
                    )
                )
            )
        }
    }

    fun approve(): DomainAggregateWithEvents<Order, Any> {
        return DomainAggregateWithEvents(
            copy(
                orderState = OrderState.APPROVED,
                orderDetails = orderDetails
            ), sequenceOf(
                OrderApprovedEvent(
                    orderDetails = orderDetails
                )
            )
        )
    }

    fun reject(): DomainAggregateWithEvents<Order, Any> {
        return DomainAggregateWithEvents(
            copy(
                orderState = OrderState.REJECTED,
                orderDetails = orderDetails
            ), sequenceOf(
                OrderRejectedEvent(
                    orderDetails = orderDetails
                )
            )
        )
    }

    fun cancel(): DomainAggregateWithEvents<Order, Any> {
        when (orderState) {
            OrderState.PENDING -> throw PendingOrderCantBeCancelledException()
            OrderState.APPROVED -> return DomainAggregateWithEvents(
                copy(
                    orderState = OrderState.CANCELLED,
                    orderDetails = orderDetails
                ), sequenceOf(
                    OrderCancelledEvent(
                        orderDetails = orderDetails
                    )
                )
            )
            else -> throw OrderCantBeCancelledAnymoreException()
        }
    }
}

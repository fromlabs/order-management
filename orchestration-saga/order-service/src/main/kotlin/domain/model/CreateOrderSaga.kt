package org.fromlabs.demo.ordermanagement.order.domain.model

data class CreateOrderSaga(
    val id: Long = 0,
    val version: Int = 0,
    val orderId: Long,
    val orderDetails: OrderDetails
) {
    companion object {
        @JvmStatic
        fun create(orderId: Long, customerId: Long, orderTotal: Money): CreateOrderSaga {
            val orderDetails = OrderDetails(
                customerId = customerId,
                orderTotal = orderTotal
            )

            return CreateOrderSaga(
                orderId = orderId,
                orderDetails = orderDetails
            )
        }
    }
}
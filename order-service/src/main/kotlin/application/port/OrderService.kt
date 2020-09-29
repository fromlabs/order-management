package org.fromlabs.demo.ordermanagement.order.application.port

import io.micronaut.core.annotation.Introspected
import org.fromlabs.demo.ordermanagement.order.domain.model.Money
import org.fromlabs.demo.ordermanagement.order.domain.model.OrderState
import javax.validation.constraints.NotNull

@Introspected
data class CreateOrderRequestDto(
    @field:NotNull val customerId: Long,
    @field:NotNull val orderTotal: Money
)

data class CreateOrderResponseDto(val orderId: Long)

data class GetOrderResponseDto(val orderId: Long, val orderState: OrderState)

data class CancelOrderResponseDto(val orderId: Long, val orderState: OrderState)

interface OrderService {
    fun getOrder(id: Long): GetOrderResponseDto

    fun createOrder(request: CreateOrderRequestDto): CreateOrderResponseDto

    fun cancelOrder(id: Long): CancelOrderResponseDto
}
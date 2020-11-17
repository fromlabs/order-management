package org.fromlabs.demo.ordermanagement.order.application.port

import io.micronaut.core.annotation.Introspected
import org.fromlabs.demo.ordermanagement.order.domain.model.Money
import org.fromlabs.demo.ordermanagement.order.domain.model.OrderState
import javax.validation.constraints.NotNull

@Introspected
data class CreateOrderSagaRequestDto(
    @field:NotNull val customerId: Long,
    @field:NotNull val orderTotal: Money
)

data class CreateOrderSagaResponseDto(val orderId: Long)

interface CreateOrderSagaService {
    fun create(request: CreateOrderSagaRequestDto): CreateOrderSagaResponseDto
}
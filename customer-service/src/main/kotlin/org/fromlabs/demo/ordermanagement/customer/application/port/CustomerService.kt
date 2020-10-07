package org.fromlabs.demo.ordermanagement.customer.application.port

import io.micronaut.core.annotation.Introspected
import org.fromlabs.demo.ordermanagement.customer.domain.model.Money
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class CreateCustomerRequestDto(
    @field:NotBlank val name: String,
    @field:NotNull val creditLimit: Money
)

data class CreateCustomerResponseDto(val id: Long, val name: String, val creditLimit: Money)

data class ReserveCreditRequestDto(val orderId: Long, val orderTotal: Money)

interface CustomerService {
    fun createCustomer(request: CreateCustomerRequestDto): CreateCustomerResponseDto
}
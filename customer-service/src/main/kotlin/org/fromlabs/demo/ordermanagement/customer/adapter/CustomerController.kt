package org.fromlabs.demo.ordermanagement.customer.adapter

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.validation.Validated
import org.fromlabs.demo.ordermanagement.customer.application.port.CreateCustomerRequestDto
import org.fromlabs.demo.ordermanagement.customer.application.port.CreateCustomerResponseDto
import org.fromlabs.demo.ordermanagement.customer.application.port.CustomerService
import javax.validation.Valid

@Produces(MediaType.APPLICATION_JSON)
@Controller("/customers")
@Validated
class CustomerController(
    private val customerService: CustomerService
) {

    @Post("/")
    fun createCustomer(@Valid @Body request: CreateCustomerRequestDto): CreateCustomerResponseDto {
        return customerService.createCustomer(request)
    }
}
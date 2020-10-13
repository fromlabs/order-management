package org.fromlabs.demo.ordermanagement.order.adapter

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import org.fromlabs.demo.ordermanagement.order.application.port.*
import javax.validation.Valid

@Produces(MediaType.APPLICATION_JSON)
@Controller("/orders")
@Validated
class OrderController(
    private val orderService: OrderService
) {

    @Get("/{id}")
    fun getOrder(id: Long): GetOrderResponseDto {
        return orderService.getOrder(id)
    }

    @Post("/")
    fun createOrder(@Valid @Body request: CreateOrderRequestDto): CreateOrderResponseDto {
        return orderService.createOrder(request)
    }

    @Post("/{id}/cancel")
    fun cancelOrder(id: Long): CancelOrderResponseDto {
        return orderService.cancelOrder(id)
    }
}
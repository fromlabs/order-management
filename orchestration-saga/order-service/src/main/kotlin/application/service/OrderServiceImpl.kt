package org.fromlabs.demo.ordermanagement.order.application.service

import io.micronaut.transaction.annotation.ReadOnly
import org.fromlabs.demo.ordermanagement.core.domain.AggregateNotFoundException
import org.fromlabs.demo.ordermanagement.order.application.port.*
import org.fromlabs.demo.ordermanagement.order.domain.model.Order
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Transactional
open class OrderServiceImpl(
    private val orderPersistence: OrderPersistence,
    private val orderEventPublisher: OrderEventPublisher,
    private val createOrderSagaService: CreateOrderSagaService
) : OrderService {

    @ReadOnly
    override fun getOrder(id: Long): GetOrderResponseDto {
        val order = orderPersistence.findById(id) ?: throw AggregateNotFoundException()

        return order.toGetOrderResponseDto()
    }

    override fun createOrder(request: CreateOrderRequestDto): CreateOrderResponseDto {
        val response = createOrderSagaService.create(request.toCreateOrderSagaRequestDto())

        val order = orderPersistence.findById(response.orderId) ?: throw AggregateNotFoundException()

        return order.toCreateOrderResponseDto()
    }

    override fun cancelOrder(id: Long): CancelOrderResponseDto {
        val order = orderPersistence.findById(id) ?: throw AggregateNotFoundException()

        var (newOrder, events) = order.cancel()

        newOrder = orderPersistence.update(newOrder)

        orderEventPublisher.publish(
            order = newOrder,
            events = events
        )

        return order.toCancelOrderResponseDto()
    }
}

private fun CreateOrderRequestDto.toCreateOrderSagaRequestDto(): CreateOrderSagaRequestDto =
    CreateOrderSagaRequestDto(
        customerId = customerId,
        orderTotal = orderTotal
    )

private fun Order.toCreateOrderResponseDto(): CreateOrderResponseDto =
    CreateOrderResponseDto(
        orderId = id,
        orderState = orderState
    )

private fun Order.toGetOrderResponseDto(): GetOrderResponseDto =
    GetOrderResponseDto(
        orderId = id,
        orderState = orderState
    )

private fun Order.toCancelOrderResponseDto(): CancelOrderResponseDto =
    CancelOrderResponseDto(
        orderId = id,
        orderState = orderState
    )

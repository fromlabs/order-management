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
    private val orderEventPublisher: OrderEventPublisher
) : OrderService, CreateOrderSaga {

    @ReadOnly
    override fun getOrder(id: Long): GetOrderResponseDto {
        val order = orderPersistence.findById(id) ?: throw AggregateNotFoundException()

        return order.toGetOrderResponseDto()
    }

    override fun createOrder(request: CreateOrderRequestDto): CreateOrderResponseDto {
        var (order, events) = Order.create(
            customerId = request.customerId,
            orderTotal = request.orderTotal
        )

        order = orderPersistence.create(order)

        orderEventPublisher.publish(
            order = order,
            events = events
        )

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

    override fun onCreditReservedEvent(customerId: Long, event: CreditReservedEventDto) {
        approveOrder(event.orderId)
    }

    override fun onCustomerValidationFailedEvent(customerId: Long, event: CustomerValidationFailedEventDto) {
        rejectOrder(event.orderId)
    }

    override fun onCreditReservationFailedEvent(customerId: Long, event: CreditReservationFailedEventDto) {
        rejectOrder(event.orderId)
    }

    private fun approveOrder(id: Long) {
        val order = orderPersistence.findById(id) ?: throw AggregateNotFoundException()

        var (newOrder, events) = order.approve()

        newOrder = orderPersistence.update(newOrder)

        orderEventPublisher.publish(
            order = newOrder,
            events = events
        )
    }

    private fun rejectOrder(id: Long) {
        val order = orderPersistence.findById(id) ?: throw AggregateNotFoundException()

        var (newOrder, events) = order.reject()

        newOrder = orderPersistence.update(newOrder)

        orderEventPublisher.publish(
            order = newOrder,
            events = events
        )
    }
}

private fun Order.toCreateOrderResponseDto(): CreateOrderResponseDto =
    CreateOrderResponseDto(orderId = id)

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

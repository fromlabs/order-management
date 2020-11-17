package org.fromlabs.demo.ordermanagement.order.application.service

import org.fromlabs.demo.ordermanagement.order.application.port.*
import org.fromlabs.demo.ordermanagement.order.domain.model.CreateOrderSaga
import org.fromlabs.demo.ordermanagement.order.domain.model.Order
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Transactional
open class CreateOrderSagaServiceImpl(
    private val orderPersistence: OrderPersistence,
    private val createOrderSagaPersistence: CreateOrderSagaPersistence,
    private val orderEventPublisher: OrderEventPublisher
) : CreateOrderSagaService {

    override fun create(request: CreateOrderSagaRequestDto): CreateOrderSagaResponseDto {
        // crea l'ordine in stato pending
        var (order, events) = Order.create(
            customerId = request.customerId,
            orderTotal = request.orderTotal
        )
        order = orderPersistence.create(order)

        // pubblica l'evento
        orderEventPublisher.publish(
            order = order,
            events = events
        )

        // crea la saga
        var saga = CreateOrderSaga.create(
            orderId = order.id,
            customerId = request.customerId,
            orderTotal = request.orderTotal
        )
        saga = createOrderSagaPersistence.create(saga)

        // essendo uno step "locale" possiamo andare avanti al prossimo step



        return saga.toCreateOrderSagaResponseDto()
    }
}

private fun CreateOrderSaga.toCreateOrderSagaResponseDto(): CreateOrderSagaResponseDto =
    CreateOrderSagaResponseDto(orderId = orderId)

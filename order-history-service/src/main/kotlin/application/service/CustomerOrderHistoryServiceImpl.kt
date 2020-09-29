package org.fromlabs.demo.ordermanagement.orderhistory.application.service

import org.fromlabs.demo.ordermanagement.core.domain.AggregateNotFoundException
import org.fromlabs.demo.ordermanagement.orderhistory.application.port.*
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.CustomerView
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.OrderInfo
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.OrderState
import javax.inject.Singleton

@Singleton
open class CustomerOrderHistoryServiceImpl(
    private val customerViewPersistence: CustomerViewPersistence
) : CustomerOrderHistoryService {
    override fun getCustomerView(id: Long): GetCustomerViewResponseDto {
        val customerView = customerViewPersistence.findById(id) ?: throw AggregateNotFoundException()

        return customerView.toGetCustomerViewResponseDto()
    }

    override fun onCustomerCreatedEvent(customerId: Long, event: CustomerCreatedEventDto) {
        customerViewPersistence.saveCustomer(
            customerId = customerId,
            name = event.name,
            creditLimit = event.creditLimit
        )
    }

    override fun onOrderCreatedEvent(orderId: Long, event: OrderCreatedEventDto) {
        customerViewPersistence.addOrder(
            customerId = event.orderDetails.customerId,
            orderInfo = OrderInfo(
                orderId = orderId,
                state = OrderState.PENDING,
                orderTotal = event.orderDetails.orderTotal
            )
        )
    }

    override fun onOrderApprovedEvent(orderId: Long, event: OrderApprovedEventDto) {
        customerViewPersistence.updateOrder(
            customerId = event.orderDetails.customerId,
            orderInfo = OrderInfo(
                orderId = orderId,
                state = OrderState.APPROVED,
                orderTotal = event.orderDetails.orderTotal
            )
        )
    }

    override fun onOrderRejectedEvent(orderId: Long, event: OrderRejectedEventDto) {
        customerViewPersistence.updateOrder(
            customerId = event.orderDetails.customerId,
            orderInfo = OrderInfo(
                orderId = orderId,
                state = OrderState.REJECTED,
                orderTotal = event.orderDetails.orderTotal
            )
        )
    }

    override fun onOrderCancelledEvent(orderId: Long, event: OrderCancelledEventDto) {
        customerViewPersistence.updateOrder(
            customerId = event.orderDetails.customerId,
            orderInfo = OrderInfo(
                orderId = orderId,
                state = OrderState.CANCELLED,
                orderTotal = event.orderDetails.orderTotal
            )
        )
    }
}

private fun CustomerView.toGetCustomerViewResponseDto(): GetCustomerViewResponseDto =
    GetCustomerViewResponseDto(
        id = customerId,
        name = name,
        creditLimit = creditLimit,
        orders = orders.mapValues { it.value.toOrderInfoDto() }
    )

private fun OrderInfo.toOrderInfoDto(): OrderInfoDto =
    OrderInfoDto(
        orderId = orderId,
        state = state,
        orderTotal = orderTotal
    )
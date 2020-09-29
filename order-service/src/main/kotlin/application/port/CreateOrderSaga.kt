package org.fromlabs.demo.ordermanagement.order.application.port

data class CreditReservedEventDto(
    val orderId: Long
)

data class CustomerValidationFailedEventDto(
    val orderId: Long
)

data class CreditReservationFailedEventDto(
    val orderId: Long
)

interface CreateOrderSaga {
    fun onCreditReservedEvent(customerId: Long, event: CreditReservedEventDto)

    fun onCustomerValidationFailedEvent(customerId: Long, event: CustomerValidationFailedEventDto)

    fun onCreditReservationFailedEvent(customerId: Long, event: CreditReservationFailedEventDto)
}
package org.fromlabs.demo.ordermanagement.customer.application.service

import org.fromlabs.demo.ordermanagement.core.domain.AggregateNotFoundException
import org.fromlabs.demo.ordermanagement.customer.application.port.*
import org.fromlabs.demo.ordermanagement.customer.domain.model.CreditReservationFailedEvent
import org.fromlabs.demo.ordermanagement.customer.domain.model.Customer
import org.fromlabs.demo.ordermanagement.customer.domain.model.CustomerCreditLimitExceededException
import org.fromlabs.demo.ordermanagement.customer.domain.model.CustomerValidationFailedEvent
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Transactional
open class CustomerServiceImpl(
    private val customerPersistence: CustomerPersistence,
    private val customerEventPublisher: CustomerEventPublisher
) : CustomerService, CreateOrderSaga {

    override fun createCustomer(request: CreateCustomerRequestDto): CreateCustomerResponseDto {
        var (customer, events) = Customer.create(name = request.name, creditLimit = request.creditLimit)

        customer = customerPersistence.create(customer)

        customerEventPublisher.publish(customer.id, events);

        return customer.toCreateCustomerResponseDto()
    }

    override fun onOrderCreatedEvent(orderId: Long, event: OrderCreatedEventDto) {
        try {
            reserveCredit(
                event.orderDetails.customerId,
                ReserveCreditRequestDto(orderId = orderId, orderTotal = event.orderDetails.orderTotal)
            )
        } catch (e: AggregateNotFoundException) {
            customerEventPublisher.publish(
                event.orderDetails.customerId, sequenceOf(CustomerValidationFailedEvent(orderId))
            )
        } catch (e: CustomerCreditLimitExceededException) {
            customerEventPublisher.publish(
                event.orderDetails.customerId, sequenceOf(CreditReservationFailedEvent(orderId))
            )
        }
    }

    private fun reserveCredit(customerId: Long, request: ReserveCreditRequestDto) {
        val customer = customerPersistence.findById(customerId) ?: throw AggregateNotFoundException()

        val (newCustomer, events) = customer.reserveCredit(
            orderId = request.orderId,
            orderTotal = request.orderTotal
        )

        customerPersistence.update(newCustomer)

        customerEventPublisher.publish(customerId, events);
    }
}

private fun Customer.toCreateCustomerResponseDto(): CreateCustomerResponseDto =
    CreateCustomerResponseDto(
        id = this.id,
        name = this.name,
        creditLimit = this.creditLimit
    )

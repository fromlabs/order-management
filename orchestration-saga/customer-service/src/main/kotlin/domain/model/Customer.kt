package org.fromlabs.demo.ordermanagement.customer.domain.model

import org.fromlabs.demo.ordermanagement.core.domain.DomainAggregateWithEvents
import org.fromlabs.demo.ordermanagement.core.domain.DomainEvent
import java.math.BigDecimal
import java.time.Instant
import javax.validation.constraints.NotNull

data class Money(@field:NotNull val amount: BigDecimal) {
    operator fun plus(moneyToAdd: Money): Money {
        return Money(amount + moneyToAdd.amount)
    }

    operator fun minus(moneyToSub: Money): Money {
        return Money(amount - moneyToSub.amount)
    }

    operator fun compareTo(other: Money): Int {
        return amount.compareTo(other.amount)
    }
}

@DomainEvent
data class CustomerCreatedEvent(
    val name: String,
    val creditLimit: Money
)

@DomainEvent
data class CreditReservedEvent(
    val orderId: Long
)

@DomainEvent
data class CustomerValidationFailedEvent(
    val orderId: Long
)

@DomainEvent
data class CreditReservationFailedEvent(
    val orderId: Long
)

class CustomerCreditLimitExceededException : Exception()

data class Customer(
    val id: Long = 0,
    val version: Int = 0,
    val name: String,
    val creditLimit: Money,
    val creditReservations: Map<Long, Money> = mapOf(),
    val creationTime: Instant = Instant.now()
) {

    val availableCredit
        get() = creditLimit - reservedCredit

    val reservedCredit by lazy {
        this.creditReservations.values.fold(
            initial = Money(BigDecimal.ZERO)
        ) { total, amount ->
            total + amount
        }
    }

    companion object {
        const val AGGREGATE_TYPE: String = "customer"

        @JvmStatic
        fun create(name: String, creditLimit: Money): DomainAggregateWithEvents<Customer, Any> {
            return DomainAggregateWithEvents(
                Customer(
                    name = name,
                    creditLimit = creditLimit
                ), sequenceOf(
                    CustomerCreatedEvent(
                        name = name,
                        creditLimit = creditLimit
                    )
                )
            )
        }
    }

    fun reserveCredit(orderId: Long, orderTotal: Money): DomainAggregateWithEvents<Customer, Any> {
        if (availableCredit >= orderTotal) {
            return DomainAggregateWithEvents(
                copy(
                    creditReservations = creditReservations.plus(orderId to orderTotal)
                ), sequenceOf(
                    CreditReservedEvent(orderId = orderId)
                )
            )
        } else {
            throw CustomerCreditLimitExceededException()
        }
    }
}

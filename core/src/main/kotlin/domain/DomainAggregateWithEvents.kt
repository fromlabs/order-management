package org.fromlabs.demo.ordermanagement.core.domain

data class DomainAggregateWithEvents<out A, out E>(
    val aggregate: A,
    val events: Sequence<E>
)
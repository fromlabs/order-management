package org.fromlabs.demo.ordermanagement.core.domain

data class DomainEventEnvelope<I, E>(val aggregateType: String, val aggregateId: I, val event: E)

interface DomainEventPublisher {
    fun publish(domain: String, aggregateType: String, aggregateId: Any, events: Sequence<Any>)
}
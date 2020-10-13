package org.fromlabs.demo.ordermanagement.customer.application.port

interface CustomerEventPublisher {
    fun publish(customerId: Long, events: Sequence<Any>)
}
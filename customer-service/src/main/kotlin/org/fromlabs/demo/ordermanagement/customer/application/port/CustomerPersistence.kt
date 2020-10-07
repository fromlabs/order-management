package org.fromlabs.demo.ordermanagement.customer.application.port

import org.fromlabs.demo.ordermanagement.customer.domain.model.Customer

interface CustomerPersistence {
    fun findById(id: Long): Customer?

    fun create(customer: Customer): Customer

    fun update(customer: Customer): Customer
}
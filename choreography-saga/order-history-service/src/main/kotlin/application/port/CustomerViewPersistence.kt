package org.fromlabs.demo.ordermanagement.orderhistory.application.port

import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.CustomerView
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.Money
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.OrderInfo

interface CustomerViewPersistence {
    fun findById(customerId: Long): CustomerView?

    fun saveCustomer(customerId: Long, name: String, creditLimit: Money): CustomerView

    fun addOrder(customerId: Long, orderInfo: OrderInfo): CustomerView

    fun updateOrder(customerId: Long, orderInfo: OrderInfo): CustomerView
}
package org.fromlabs.demo.ordermanagement.order

import io.micronaut.test.annotation.MicronautTest
import org.fromlabs.demo.ordermanagement.order.adapter.OrderRepository
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class OrderRepositoryTest {

    @Inject
    lateinit var orderRepository: OrderRepository

    @Test
    fun testItWorks() {
        orderRepository.findAll()
    }
}
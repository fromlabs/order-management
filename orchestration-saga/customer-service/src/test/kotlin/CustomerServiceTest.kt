package org.fromlabs.demo.ordermanagement.customer

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.fromlabs.demo.ordermanagement.customer.application.port.CustomerService
import javax.inject.Inject

@MicronautTest
class CustomerServiceTest {

    @Inject
    lateinit var customerService: CustomerService
}
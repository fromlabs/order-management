package org.fromlabs.demo.ordermanagement.customer

import com.nhaarman.mockitokotlin2.mock
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.fromlabs.demo.ordermanagement.customer.application.port.CustomerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class CustomerControllerTest {

    @Inject
    lateinit var customerService: CustomerService

    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

    @MockBean(CustomerService::class)
    fun customerService(): CustomerService = mock()

    @Test
    fun testUsers() {
        val request: HttpRequest<Any> = HttpRequest.GET("/users")
        val body = client.toBlocking().retrieve(request)
        Assertions.assertNotNull(body)
        Assertions.assertEquals("{}", body)
    }
}
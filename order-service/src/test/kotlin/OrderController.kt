package org.fromlabs.demo.ordermanagement.order

import com.nhaarman.mockitokotlin2.mock
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import org.fromlabs.demo.ordermanagement.order.application.port.OrderService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class OrderController {

    @Inject
    lateinit var orderService: OrderService

    @Inject
    @field:Client("/orders")
    lateinit var client: RxHttpClient

    @MockBean(OrderService::class)
    fun orderService(): OrderService = mock()

    @Test
    fun testGetOrder() {
        val request: HttpRequest<Any> = HttpRequest.GET("/1")
        val body = client.toBlocking().retrieve(request)
        Assertions.assertNotNull(body)
        Assertions.assertEquals("{}", body)
    }
}
package org.fromlabs.demo.ordermanagement.orderhistory

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Order History Service",
        version = "0.1.0",
        description = "Order History Service API"
    )
)
object OrderHistoryApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(OrderHistoryApplication.javaClass)
    }
}


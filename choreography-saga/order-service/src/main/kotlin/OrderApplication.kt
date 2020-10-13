package org.fromlabs.demo.ordermanagement.order

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Order Service",
        version = "0.1.0",
        description = "Order Service API"
    )
)
object OrderApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(OrderApplication.javaClass)
    }
}


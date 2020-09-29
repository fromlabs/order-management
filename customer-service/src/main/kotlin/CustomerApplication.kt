package org.fromlabs.demo.ordermanagement.customer

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Customer Service",
        version = "0.1.0",
        description = "Customer Service API"
    )
)
object CustomerApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(CustomerApplication.javaClass)
    }
}


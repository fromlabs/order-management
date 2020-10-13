package org.fromlabs.demo.ordermanagement.orderhistory.adapter

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import org.fromlabs.demo.ordermanagement.orderhistory.application.port.CustomerOrderHistoryService
import org.fromlabs.demo.ordermanagement.orderhistory.application.port.GetCustomerViewResponseDto

@Produces(MediaType.APPLICATION_JSON)
@Controller("/customers")
class CustomerOrderHistoryController(
    private val customerOrderHistoryService: CustomerOrderHistoryService
) {

    @Get("/{id}")
    fun getCustomerView(id: Long): GetCustomerViewResponseDto {
        return customerOrderHistoryService.getCustomerView(id)
    }
}
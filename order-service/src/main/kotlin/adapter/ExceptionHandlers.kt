package org.fromlabs.demo.ordermanagement.order.adapter

import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import org.fromlabs.demo.ordermanagement.order.domain.model.OrderCantBeCancelledAnymoreException
import org.fromlabs.demo.ordermanagement.order.domain.model.PendingOrderCantBeCancelledException
import javax.inject.Singleton

@Produces
@Singleton
@Requirements(
    Requires(classes = [PendingOrderCantBeCancelledException::class, ExceptionHandler::class])
)
class PendingOrderCantBeCancelledExceptionHandler :
    ExceptionHandler<PendingOrderCantBeCancelledException, HttpResponse<*>> {

    override fun handle(
        request: HttpRequest<*>,
        exception: PendingOrderCantBeCancelledException
    ): HttpResponse<String> {
        return HttpResponse.badRequest("Pending order can't be cancelled")
    }
}

@Produces
@Singleton
@Requirements(
    Requires(classes = [OrderCantBeCancelledAnymoreException::class, ExceptionHandler::class])
)
class OrderCantBeCancelledAnymoreExceptionHandler :
    ExceptionHandler<OrderCantBeCancelledAnymoreException, HttpResponse<*>> {

    override fun handle(
        request: HttpRequest<*>,
        exception: OrderCantBeCancelledAnymoreException
    ): HttpResponse<String> {
        return HttpResponse.badRequest("Order can't be cancelled anymore")
    }
}
package org.fromlabs.demo.ordermanagement.order.adapter

import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.hateoas.Link
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
    ): HttpResponse<JsonError> {
        val error = JsonError("Pending order can't be cancelled")
        error.link(Link.SELF, Link.of(request.uri))
        return HttpResponse.badRequest(error)
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
    ): HttpResponse<JsonError> {
        val error = JsonError("Order can't be cancelled anymore")
        error.link(Link.SELF, Link.of(request.uri))
        return HttpResponse.badRequest(error)
    }
}
package org.fromlabs.demo.ordermanagement.core.rest

import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import org.fromlabs.demo.ordermanagement.core.domain.AggregateNotFoundException
import javax.inject.Singleton

@Produces
@Singleton
@Requirements(
    Requires(classes = [AggregateNotFoundException::class, ExceptionHandler::class])
)
class ExceptionHandlers : ExceptionHandler<AggregateNotFoundException, HttpResponse<*>> {

    override fun handle(request: HttpRequest<*>, exception: AggregateNotFoundException): HttpResponse<Unit> {
        return HttpResponse.notFound()
    }
}
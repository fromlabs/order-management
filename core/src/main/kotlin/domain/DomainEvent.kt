package org.fromlabs.demo.ordermanagement.core.domain

import io.micronaut.core.annotation.Introspected

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Introspected
annotation class DomainEvent(val eventType: String = "")
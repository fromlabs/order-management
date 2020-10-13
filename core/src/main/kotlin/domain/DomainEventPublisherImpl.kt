package org.fromlabs.demo.ordermanagement.core.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.CaseFormat
import com.google.common.base.Converter
import io.micronaut.core.beans.BeanIntrospection
import org.fromlabs.demo.ordermanagement.core.messaging.MessageData
import org.fromlabs.demo.ordermanagement.core.messaging.MessageDataHeader
import org.fromlabs.demo.ordermanagement.core.messaging.MessageProducer
import java.beans.IntrospectionException
import java.time.Instant
import javax.inject.Singleton

private val eventTypeConverter: Converter<String, String> =
    CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN)

@Singleton
class DomainEventPublisherImpl(
    private val messageProducer: MessageProducer,
    private val objectMapper: ObjectMapper
) : DomainEventPublisher {
    init {
        println("DomainEventPublisherImpl()");
    }

    override fun publish(domain: String, aggregateType: String, aggregateId: Any, events: Sequence<Any>) {
        messageProducer.send(events.map { event: Any ->
            val eventType: String = resolveDomainEventType(event)

            val envelope = DomainEventEnvelope(
                aggregateType = aggregateType,
                aggregateId = aggregateId,
                event = event
            )

            val serializedEnvelope: String = objectMapper.writeValueAsString(envelope)

            MessageData(
                topic = "$domain.$aggregateType.events",
                timestamp = Instant.now(),
                headers = listOf(
                    MessageDataHeader("x-event-type", eventType)
                ),
                key = aggregateId.toString(),
                payload = serializedEnvelope
            )
        })
    }

    private fun resolveDomainEventType(event: Any): String {
        try {
            val introspection = BeanIntrospection.getIntrospection(event::class.java)
            val annotation = introspection.getAnnotation(DomainEvent::class.java)
            if (annotation != null) {
                return annotation.stringValue("eventType").orElseGet {
                    var eventType: String = eventTypeConverter.convert(event.javaClass.simpleName)!!

                    if (eventType.endsWith("-event")) {
                        eventType = eventType.substring(0, eventType.length - "-event".length)
                    }

                    eventType
                }
            } else {
                throw IllegalArgumentException("Event not annotated with @DomainEvent")
            }
        } catch (e: IntrospectionException) {
            throw IllegalArgumentException("Event not annotated with @DomainEvent", e)
        }
    }
}
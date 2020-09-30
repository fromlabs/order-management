package org.fromlabs.demo.ordermanagement.core.messaging

import java.time.Instant

// x-event-type
// x-command-type
// b3

data class MessageData(
    val topic: String,
    val timestamp: Instant,
    val headers: List<MessageDataHeader>,
    val key: String,
    val payload: String
)

data class MessageDataHeader(
    val name: String,
    val value: String
)

interface MessageProducer {
    fun send(messages: Sequence<MessageData>)
}
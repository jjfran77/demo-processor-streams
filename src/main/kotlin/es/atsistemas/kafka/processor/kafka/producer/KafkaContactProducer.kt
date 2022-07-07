package es.atsistemas.kafka.processor.kafka.producer

import es.atsistemas.kafka.processor.mapper.ContactMapper
import es.atsistemas.kafka.processor.model.Contact
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.OffsetDateTime

/**
 * @author Juan Francisco Guerrero (jfguerrrero@atsistemas.com)
 */
@Component
class KafkaContactProducer(private val contactProducerSink: Sinks.Many<Message<ByteArray>>, private val contactMapper: ContactMapper) {

    fun send(contact: Contact): Mono<Void> {

        val dateTime = OffsetDateTime.now().toInstant().toEpochMilli()

        val key = contact.nif
        val value = contactMapper.toJson(contact)

        val message = MessageBuilder.withPayload(value.toByteArray())
            .setHeader(KafkaHeaders.MESSAGE_KEY, key.toByteArray())
            .setHeader(KafkaHeaders.TIMESTAMP, dateTime)
            .build()

        contactProducerSink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST)
        return Mono.empty()
    }



}

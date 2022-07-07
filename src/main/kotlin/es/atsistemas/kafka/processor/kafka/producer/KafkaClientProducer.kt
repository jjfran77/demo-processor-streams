package es.atsistemas.kafka.processor.kafka.producer

import es.atsistemas.kafka.processor.mapper.ClientMapper
import es.atsistemas.kafka.processor.model.Client
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
class KafkaClientProducer(private val clientProducerSink: Sinks.Many<Message<ByteArray>>, private val clientMapper: ClientMapper) {

    fun sendProcessedText(key: String, client: Client): Mono<Void> {
        val dateTime = OffsetDateTime.now().toInstant().toEpochMilli()

        val value = clientMapper.toJson(client)

        val message = MessageBuilder.withPayload(value.toByteArray())
            .setHeader(KafkaHeaders.MESSAGE_KEY, key.toByteArray())
            .setHeader(KafkaHeaders.TIMESTAMP, dateTime)
            .build()

        clientProducerSink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST)
        return Mono.empty()
    }


}
package es.atsistemas.kafka.processor.kafka.stream

import es.atsistemas.kafka.processor.kafka.producer.KafkaClientProducer
import es.atsistemas.kafka.processor.mapper.ClientMapper
import es.atsistemas.kafka.processor.mapper.ContactMapper
import es.atsistemas.kafka.processor.model.Contact
import es.atsistemas.kafka.processor.repository.ClientRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.function.Consumer

/**
 * @author Juan Francisco Guerrero (jfguerrrero@atsistemas.com)
 */
@Configuration
class ProcessorStream {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun process(KafkaProcessedProducer : KafkaClientProducer, clientRepository: ClientRepository,
                clientMapper: ClientMapper, contactMapper: ContactMapper): Consumer<Message<String?>?>  {
        return Consumer<Message<String?>?> {

            msg: Message<String?>? ->  LoggerFactory.getLogger(javaClass).debug("consumed message <{}>",msg)

            if (msg != null) {

                val key = getKey(msg)
                val value = getValue(msg)

                var contact: Contact = contactMapper.toContact(value)

                var client = clientMapper.mapContactToClient(contact)
                client.id = "client::" + client.nif
                client.bankAccount = "es12439043940"
                client.modality = "premium"

                clientRepository.save(client)


                KafkaProcessedProducer.sendProcessedText(key, client)

            }

        }
    }

    private fun getValue(msg: Message<String?>) = msg.payload.toString()

    private fun getKey(msg: Message<String?>): String{
        val byteArray = msg.headers.get("kafka_receivedMessageKey")
        val key = String(byteArray as ByteArray)
        return key
    }


}

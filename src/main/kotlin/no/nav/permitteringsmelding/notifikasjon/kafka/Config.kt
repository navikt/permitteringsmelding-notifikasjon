package no.nav.permitteringsmelding.notifikasjon.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.Deserializer

const val permitteringsmeldingtopic = "permittering-og-nedbemanning.aapen-permittering-arbeidsgiver"

val COMMON_PROPERTIES = mapOf(
    CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to (System.getenv("KAFKA_BROKERS") ?: "localhost:9092"),
    CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG to "500",
    CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG to "500",
    CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_CONFIG to "5000",
)

val SSL_PROPERTIES = if (System.getenv("KAFKA_KEYSTORE_PATH").isNullOrBlank())
    emptyMap()
else
    mapOf(
        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SSL",
        SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG to "",
        SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG to System.getenv("KAFKA_KEYSTORE_PATH"),
        SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG to System.getenv("KAFKA_CREDSTORE_PASSWORD"),
        SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG to System.getenv("KAFKA_TRUSTSTORE_PATH"),
        SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG to System.getenv("KAFKA_CREDSTORE_PASSWORD"),
    )


val CONSUMER_PROPERTIES = COMMON_PROPERTIES + SSL_PROPERTIES + mapOf(
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
    ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 1,
    ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG to Int.MAX_VALUE,
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "false"
)

class PermitteringsMeldingDeserializer : JsonDeserializer<PermitteringsMelding>(PermitteringsMelding::class.java)

val kafkaObjectMapper = jacksonObjectMapper().apply {
    //registerModule(JavaTimeModule())
    //disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    //disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}

abstract class JsonDeserializer<T>(private val clazz: Class<T>) : Deserializer<T> {
    override fun deserialize(topic: String?, data: ByteArray?): T {
        return kafkaObjectMapper.readValue(data, clazz)
    }
}
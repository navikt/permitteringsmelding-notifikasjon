package no.nav.permitteringsmelding.notifikasjon.kafka

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import no.nav.permitteringsmelding.notifikasjon.Health
import no.nav.permitteringsmelding.notifikasjon.Metrics
import no.nav.permitteringsmelding.notifikasjon.log
import no.nav.permitteringsmelding.notifikasjon.toThePowerOf
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.Deserializer
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.schedule

class CoroutineKafkaConsumer<K, V>
private constructor(
    topic: String,
    groupId: String,
    keyDeserializer: Class<*>,
    valueDeserializer: Class<*>,
    private val configure: Properties.() -> Unit = {},
) {
    private val pollBodyTimer = Timer.builder("kafka.poll.body")
        .register(Metrics.meterRegistry)

    private val iterationEntryCounter = Counter.builder("kafka.poll.body.iteration")
        .tag("point", "entry")
        .register(Metrics.meterRegistry)
    private val iterationExitCounter = Counter.builder("kafka.poll.body.iteration")
        .tag("point", "exit")
        .register(Metrics.meterRegistry)

    private val kafkaContext = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    companion object {
        fun <K, V, KS : Deserializer<K>, VS: Deserializer<V>> new(
            topic: String,
            groupId: String,
            keyDeserializer: Class<KS>,
            valueDeserializer: Class<VS>,
            configure: Properties.() -> Unit = {},
        ): CoroutineKafkaConsumer<K, V> = CoroutineKafkaConsumer(
            topic,
            groupId,
            keyDeserializer,
            valueDeserializer,
            configure
        )
    }

    private val properties = Properties().apply {
        putAll(CONSUMER_PROPERTIES)
        this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = keyDeserializer.canonicalName
        this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = valueDeserializer.canonicalName
        this[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        configure()
    }

    private val consumer: Consumer<K, V> = KafkaConsumer(properties)

    init {
        KafkaClientMetrics(consumer).bindTo(Metrics.meterRegistry)
        consumer.subscribe(listOf(topic))
    }


    private val retriesPerPartition = ConcurrentHashMap<Int, AtomicInteger>()

    private val resumeQueue = ConcurrentLinkedQueue<TopicPartition>()

    private val retryTimer = Timer()

    suspend fun forEach(
        stop: AtomicBoolean = AtomicBoolean(false),
        body: suspend (ConsumerRecord<K, V>) -> Unit
    ) {
        withContext(kafkaContext) {
            while (!stop.get() && !Health.terminating) {
                consumer.resume(resumeQueue.pollAll())
                val records = try {
                    consumer.poll(Duration.ofMillis(1000))
                } catch (e: Exception) {
                    log.error("Unrecoverable error during poll {}", consumer.assignment(), e)
                    throw e
                }

                pollBodyTimer.record(Runnable {
                    forEachRecord(records, body)
                })
            }

        }
    }

    private fun forEachRecord(
        records: ConsumerRecords<K, V>,
        body: suspend (ConsumerRecord<K, V>) -> Unit
    ) {
        if (records.isEmpty) {
            return
        }

        records.partitions().forEach currentPartition@{ partition ->
            val retries = retriesForPartition(partition.partition())
            records.records(partition).forEach currentRecord@{ record ->
                try {
                    log.info("processing {}", record.loggableToString())
                    iterationEntryCounter.increment()
                    runBlocking(Dispatchers.IO) {
                        body(record)
                    }
                    consumer.commitSync(mapOf(partition to OffsetAndMetadata(record.offset() + 1)))
                    iterationExitCounter.increment()
                    log.info("successfully processed {}", record.loggableToString())
                    retries.set(0)
                    return@currentRecord
                } catch (e: Exception) {
                    val attempt = retries.incrementAndGet()
                    val backoffMillis = 1000L * 2.toThePowerOf(attempt)
                    log.error(
                        "exception while processing {}. attempt={}. backoff={}.",
                        record.loggableToString(),
                        attempt,
                        Duration.ofMillis(backoffMillis),
                        e
                    )
                    val currentPartition = TopicPartition(record.topic(), record.partition())
                    consumer.seek(currentPartition, record.offset())
                    consumer.pause(listOf(currentPartition))
                    retryTimer.schedule(backoffMillis) {
                        resumeQueue.offer(currentPartition)
                    }
                    return@currentPartition
                }
            }
        }
    }

    private fun retriesForPartition(partition: Int) =
        retriesPerPartition.getOrPut(partition) {
            AtomicInteger(0).also { retries ->
                Metrics.meterRegistry.gauge(
                    "kafka_consumer_retries_per_partition",
                    Tags.of(Tag.of("partition", partition.toString())),
                    retries
                )
            }
        }
}

private fun <T> ConcurrentLinkedQueue<T>.pollAll(): List<T> =
    generateSequence {
        this.poll()
    }.toList()

internal fun <K, V> ConsumerRecord<K, V>.loggableToString() = """
        |ConsumerRecord(
        |    topic = ${topic()},
        |    partition = ${partition()}, 
        |    offset = ${offset()},
        |    timestamp = ${timestamp()},
        |    key = ${key()}
        |    value = ${loggableValue()})
    """.trimMargin()

private fun <K, V> ConsumerRecord<K, V>.loggableValue() : String {
    return when (val value = value()) {
        null -> "Tombstone"
        else -> value!!::class.java.simpleName
    }
}
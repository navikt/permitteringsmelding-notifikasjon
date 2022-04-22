package no.nav.permitteringsmelding.notifikasjon.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.permitteringsmelding.notifikasjon.minsideklient.MinSideNotifikasjonerService
import no.nav.permitteringsmelding.notifikasjon.utils.log
import no.nav.permitteringsmelding.notifikasjon.utils.urlTilPermitteringsløsningFrontend
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.errors.WakeupException
import java.io.Closeable
import java.time.Duration

class PermitteringsmeldingConsumer(
        private val consumer: Consumer<String, String>,
        private val minSideNotifikasjonerService: MinSideNotifikasjonerService
) : Closeable {
    val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    suspend fun start() {
        try {
            log.info("forsøker å konsumere topic")
            consumer.subscribe(listOf(permitteringsmeldingtopic))
            log.info("Starter å konsumere topic $permitteringsmeldingtopic med groupId ${consumer.groupMetadata().groupId()}")
            while (true) {
                val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofSeconds(5))

                if (records.count() == 0) continue
                consumer.commitSync()
                records.forEach{melding ->
                    val permitteringsMelding: PermitteringsMelding = mapper.readValue(melding.value())
                    minSideNotifikasjonerService.opprettNySak(permitteringsMelding.id, permitteringsMelding.type, permitteringsMelding.bedriftsnummer, "Melding mottatt, gratulerer", urlTilPermitteringsløsningFrontend+ permitteringsMelding.id,permitteringsMelding.sendtInnTidspunkt)
                }
                //log.info("Committet offset ${records.last().offset()} til Kafka")
            }
        } catch (exception: WakeupException) {
            log.info("Fikk beskjed om å lukke consument med groupId ${consumer.groupMetadata().groupId()}")
        } catch (exception: Exception) {
            log.info("Noe gikk galt. Feilet med exception", exception)

            //Liveness.kill("Noe galt skjedde i konsument", exception)
        } finally {
            log.info("konsumenten lukkes")
            consumer.close()
        }
    }

    override fun close() {
        // Vil kaste WakeupException i konsument slik at den stopper, thread-safe.
        consumer.wakeup()
    }
}
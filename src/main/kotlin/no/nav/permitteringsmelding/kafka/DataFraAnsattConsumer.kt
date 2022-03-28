package no.nav.permitteringsmelding.kafka

import no.nav.permitteringsmelding.kafka.permitteringsmeldingtopic
import no.nav.permitteringsportal.DataFraAnsatt
import no.nav.permitteringsportal.database.Repository
import no.nav.permitteringsportal.minsideklient.MinSideNotifikasjonerService
import no.nav.permitteringsportal.utils.log
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.errors.WakeupException
import java.io.Closeable
import java.time.Duration

class DataFraAnsattConsumer(
        private val consumer: Consumer<String, DataFraAnsatt>,
        private val repository: Repository,
        private val minSideNotifikasjonerService: MinSideNotifikasjonerService
) : Closeable {

    fun start() {
        try {
            consumer.subscribe(listOf(permitteringsmeldingtopic))
            log.info("Starter å konsumere topic $permitteringsmeldingtopic med groupId ${consumer.groupMetadata().groupId()}")
            while (true) {
                val records: ConsumerRecords<String, DataFraAnsatt> = consumer.poll(Duration.ofSeconds(5))
                if (records.count() == 0) continue
                consumer.commitSync()
                records.forEach{
                    //send til minside
                }
                log.info("Committet offset ${records.last().offset()} til Kafka")
            }
        } catch (exception: WakeupException) {
            log.info("Fikk beskjed om å lukke consument med groupId ${consumer.groupMetadata().groupId()}")
        } catch (exception: Exception) {

            //Liveness.kill("Noe galt skjedde i konsument", exception)
        } finally {
            consumer.close()
        }
    }

    override fun close() {
        // Vil kaste WakeupException i konsument slik at den stopper, thread-safe.
        consumer.wakeup()
    }
}
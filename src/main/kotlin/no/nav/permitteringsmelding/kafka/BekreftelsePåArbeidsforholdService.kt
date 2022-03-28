package no.nav.permitteringsportal.kafka

import no.nav.permitteringsportal.database.BekreftelsePåArbeidsforhold
import no.nav.permitteringsportal.utils.log
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
val svarTopic = "dagpengedok"

class BekreftelsePåArbeidsforholdService(
    private val producer: Producer<String, BekreftelsePåArbeidsforhold>,
    private val usendteForespørsler: List<BekreftelsePåArbeidsforhold>
) {
        fun sendUsendte() {
            if (usendteForespørsler.isNotEmpty()) {
                log.info("Fant ${usendteForespørsler.size} usendte forespørsler")
            }

            usendteForespørsler
                .forEach { usendtForespørsel ->
                        val melding = ProducerRecord(svarTopic, usendtForespørsel.id.toString(), usendtForespørsel)

                        producer.send(melding) { _, exception ->
                            if (exception == null) {
                                log.info("Sendte melding")
                            } else {
                                log.error("Det skjedde noe feil under sending til Kafka", exception)
                            }
                        }
                    }
                }
}

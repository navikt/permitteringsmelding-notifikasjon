package no.nav.permitteringsmelding.notifikasjon

import no.nav.permitteringsmelding.notifikasjon.kafka.CoroutineKafkaConsumer
import no.nav.permitteringsmelding.notifikasjon.kafka.PermitteringsMeldingDeserializer
import no.nav.permitteringsmelding.notifikasjon.kafka.permitteringsmeldingtopic
import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient
import no.nav.permitteringsmelding.notifikasjon.setup.Oauth2ClientStub
import org.apache.kafka.common.serialization.StringDeserializer

fun main() {
    App.main(
        httpPort = 8880,
        consumer = CoroutineKafkaConsumer.new(
            topic = permitteringsmeldingtopic,
            groupId = "permitteringsmelding-notifikasjon1",
            keyDeserializer = StringDeserializer::class.java,
            valueDeserializer = PermitteringsMeldingDeserializer::class.java,
            configure = { },
        ),
        minsideraphQLKlient = MinSideGraphQLKlient(oauth2Client = Oauth2ClientStub()),
    )
}
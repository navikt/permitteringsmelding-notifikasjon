package no.nav.permitteringsmelding.notifikasjon

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsmelding.kafka.PermitteringsmeldingConsumer
import no.nav.permitteringsmelding.kafka.consumerConfig
import no.nav.permitteringsmelding.notifikasjon.utils.log
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2ClientImpl
import no.nav.permitteringsmelding.notifikasjon.minsideklient.MinSideNotifikasjonerService
import no.nav.permitteringsmelding.notifikasjon.minsideklient.getDefaultHttpClient
import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient
import no.nav.permitteringsmelding.notifikasjon.utils.environmentVariables
import no.nav.security.token.support.client.core.ClientAuthenticationProperties
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.io.Closeable
import kotlin.concurrent.thread

class App(private val permitteringsmeldingConsumer: PermitteringsmeldingConsumer) : Closeable {
    private val server = embeddedServer(Netty, port = 8080) {

        routing {
            get("/permitteringsmelding-notifikasjon/internal/isAlive") { call.respond(HttpStatusCode.OK) }
            get("/permitteringsmelding-notifikasjon/internal/isReady") { call.respond(HttpStatusCode.OK) }
        }
    }


    fun start() {
        // runFlywayMigrations(dataSource)
        server.start()
        runBlocking {
            permitteringsmeldingConsumer.start();
        }
    }

    override fun close() {
        log.info("Stopper app")
        server.stop(0, 0)
    }
}

// Brukes når appen kjører på Nais
fun main() {

    val azureAuthProperties = ClientAuthenticationProperties.builder()
        .clientId(System.getenv("AZURE_APP_CLIENT_ID"))
        .clientJwk(System.getenv("AZURE_APP_JWK"))
        .clientAuthMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
        .build()

    val httpClient = getDefaultHttpClient()
    val oauth2Client = Oauth2ClientImpl(httpClient, azureAuthProperties)


    val consumer: Consumer<String, String> = KafkaConsumer<String, String>(consumerConfig())

    val minSideGraphQLKlient = MinSideGraphQLKlient(environmentVariables.urlTilNotifikasjonIMiljo, httpClient, oauth2Client)
    val minSideNotifikasjonerService = MinSideNotifikasjonerService(minSideGraphQLKlient)

    val permitteringsmeldingConsumer: PermitteringsmeldingConsumer = PermitteringsmeldingConsumer(consumer, minSideNotifikasjonerService)

    App(permitteringsmeldingConsumer).start()
}

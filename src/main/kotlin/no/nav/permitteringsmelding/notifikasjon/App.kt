package no.nav.permitteringsmelding.notifikasjon

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.permitteringsmelding.notifikasjon.utils.log
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.permitteringsmelding.notifikasjon.minsideklient.MinSideNotifikasjonerService
import no.nav.permitteringsmelding.notifikasjon.minsideklient.getHttpClient
import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient
import no.nav.security.token.support.client.core.ClientAuthenticationProperties
import java.io.Closeable

class App: Closeable {
    private val server = embeddedServer(Netty, port = 8080) {

        routing {
            get("/permitteringsmelding-notifikasjon/internal/isAlive") { call.respond(HttpStatusCode.OK) }
            get("/permitteringsmelding-notifikasjon/internal/isReady") { call.respond(HttpStatusCode.OK) }
        }
    }

    fun start() {
        // runFlywayMigrations(dataSource)
        server.start()
    }

    override fun close() {
        log.info("Stopper app")
    }
}

// Brukes når appen kjører på Nais
fun main() {

    val azureAuthProperties = ClientAuthenticationProperties.builder()
        .clientId(System.getenv("AZURE_APP_CLIENT_ID"))
        .clientJwk(System.getenv("AZURE_APP_JWK"))
        .clientAuthMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
        .build()

    val httpClient = getHttpClient()
    val oauth2Client = Oauth2Client(httpClient, azureAuthProperties)

    val minSideGraphQLKlient = MinSideGraphQLKlient("localhost", httpClient, oauth2Client)
    val minSideNotifikasjonerService = MinSideNotifikasjonerService(minSideGraphQLKlient)

    App().start()
}

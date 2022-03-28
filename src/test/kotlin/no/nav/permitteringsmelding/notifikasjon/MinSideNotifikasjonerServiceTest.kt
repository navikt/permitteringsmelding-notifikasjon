package no.nav.permitteringsmelding.notifikasjon

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.permitteringsmelding.notifikasjon.minsideklient.MinSideNotifikasjonerService
import no.nav.permitteringsmelding.notifikasjon.minsideklient.getDefaultHttpClient
import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient
import no.nav.permitteringsmelding.notifikasjon.setup.getAzureTestAuthProperties
import org.junit.Ignore
import org.junit.Test

class MinSideNotifikasjonerServiceTest {

    @Ignore
    @Test
    fun `Test for å verifisere oppsett av klienter`() {
        val httpClient = getDefaultHttpClient()
        val oauth2Client = Oauth2Client(httpClient, getAzureTestAuthProperties())
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"response":"ok"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val httpClient = HttpClient(mockEngine)
            val minSideGraphQLKlient = MinSideGraphQLKlient("http://localhost:8080", httpClient, oauth2Client)
            val minSideNotifikasjonerService = MinSideNotifikasjonerService(minSideGraphQLKlient )
            minSideNotifikasjonerService.opprettNySak("1234", "Permittering", "1234", "Tittel", "Lenke")
            println("ohoy")
        }

    }

    @Test
    fun `Skal parse en melding og transformere til en notifikasjon med GraphQL`() {

    }
}
package no.nav.permitteringsmelding.notifikasjon

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.permitteringsmelding.notifikasjon.minsideklient.MinSideNotifikasjonerService
import no.nav.permitteringsmelding.notifikasjon.minsideklient.getDefaultHttpClient
import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient
import no.nav.permitteringsmelding.notifikasjon.setup.Oauth2ClientStub
import no.nav.permitteringsmelding.notifikasjon.setup.getAzureTestAuthProperties
import no.nav.permitteringsmelding.notifikasjon.setup.issuerConfig
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.junit.Ignore
import org.junit.Test

class MinSideNotifikasjonerServiceTest {

    companion object {
        private val mockOAuth2Server = MockOAuth2Server()

        init {
            mockOAuth2Server.shutdown()
            mockOAuth2Server.start()
        }
    }

    @Test
    fun `Test for å verifisere oppsett av klienter`() {
        val oauth2Client = Oauth2ClientStub()

        startLokalApp(mockk(relaxed = true)).use {
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


    }
}

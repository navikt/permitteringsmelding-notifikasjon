package no.nav.permitteringsvarsel.notifikasjon

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsvarsel.notifikasjon.minsideklient.MinSideNotifikasjonerService
import no.nav.permitteringsvarsel.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient
import org.junit.Test

class MinSideNotifikasjonerServiceTest {

    @Test
    fun `Test for å verifisere oppsett av klienter`() {

        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"response":"ok"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val httpClient = HttpClient(mockEngine)
            val minSideGraphQLKlient = MinSideGraphQLKlient("http://localhost:8080", httpClient)
            val minSideNotifikasjonerService = MinSideNotifikasjonerService(minSideGraphQLKlient )
            minSideNotifikasjonerService.sendBeskjed("1234", "1234", "1234")
            println("ohoy")
        }

    }

    @Test
    fun `Skal parse en melding og transformere til en notifikasjon med GraphQL`() {

    }
}
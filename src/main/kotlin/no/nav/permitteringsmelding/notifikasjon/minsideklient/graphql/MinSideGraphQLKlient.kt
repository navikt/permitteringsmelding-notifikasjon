package no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.ISO8601DateTime
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.OpprettNyBeskjed
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.OpprettNySak
import no.nav.permitteringsmelding.notifikasjon.utils.environmentVariables
import java.net.URL

class MinSideGraphQLKlient(val endpoint: String, val httpClient: HttpClient, val oauth2Client: Oauth2Client) {
    suspend fun opprettNySak(
        grupperingsid: String,
        merkelapp: String,
        virksomhetsnummer: String,
        tittel: String,
        lenke: String,
        tidspunkt: ISO8601DateTime? = null
    ) {
        val scopedAccessToken = oauth2Client.machine2machine(environmentVariables.azureADTokenEndpointUrl, environmentVariables.notifikasjonerScope).accessToken

        val client = GraphQLKtorClient(
            url = URL(endpoint),
            httpClient = httpClient
        )

        runBlocking {
            val query = OpprettNySak(variables = OpprettNySak.Variables(
                grupperingsid,
                merkelapp,
                virksomhetsnummer,
                tittel,
                lenke,
                tidspunkt
            ));
            val resultat = client.execute(query);

        }
        return
    }
}

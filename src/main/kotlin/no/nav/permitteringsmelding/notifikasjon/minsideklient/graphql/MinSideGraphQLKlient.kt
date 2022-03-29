package no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.ISO8601DateTime
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.OpprettNySak
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.opprettnysak.*
import no.nav.permitteringsmelding.notifikasjon.utils.log
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
        val scopedAccessToken = oauth2Client.machine2machine().accessToken

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
            val resultat = client.execute(query) {
                header(HttpHeaders.Authorization, "Bearer $scopedAccessToken")
            };
            val nySak = resultat.data?.nySak
            if(nySak !is NySakVellykket) {
                if (nySak is UgyldigMerkelapp) {
                    log.error("Feilmelding {}", nySak.feilmelding)
                } else if (nySak is UgyldigMottaker) {
                    log.error("Feilmelding {}", nySak.feilmelding)
                } else if (nySak is UkjentProdusent) {
                    log.error("Feilmelding {}", nySak.feilmelding)
                } else if (nySak is UkjentRolle) {
                    log.error("Feilmelding {}", nySak.feilmelding)
                } else if (nySak is DuplikatGrupperingsid) {
                    log.error("Feilmelding {}", nySak.feilmelding)
                }
                log.error("Kunne ikke opprette ny sak", nySak)
            } else {
                log.info("Opprettet ny sak {}", nySak.id)
            }
        }
        return
    }
}

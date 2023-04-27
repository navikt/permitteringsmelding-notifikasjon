package no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.ISO8601DateTime
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.OpprettNySak
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.opprettnysak.*
import no.nav.permitteringsmelding.notifikasjon.Env
import no.nav.permitteringsmelding.notifikasjon.log
import java.net.URL


private val defaultHttpClient = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = JacksonSerializer {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}

class MinSideGraphQLKlient(
    val endpoint: String = Env.urlTilNotifikasjonIMiljo,
    val httpClient: HttpClient = defaultHttpClient,
    val oauth2Client: Oauth2Client
) {
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
            ))
            val resultat = client.execute(query) {
                header(HttpHeaders.Authorization, "Bearer $scopedAccessToken")
            }
            val nySak = resultat.data?.nySak

            if(nySak !is NySakVellykket) {
                // TODO: should probably not continue on error like this??
                when (nySak) {
                    is DuplikatGrupperingsid -> log.error("Feilmelding {}", nySak.feilmelding)
                    is UgyldigMerkelapp -> log.error("Feilmelding {}", nySak.feilmelding)
                    is UgyldigMottaker -> log.error("Feilmelding {}", nySak.feilmelding)
                    is UkjentProdusent -> log.error("Feilmelding {}", nySak.feilmelding)
                    is UkjentRolle -> log.error("Feilmelding {}", nySak.feilmelding)
                    else -> {
                        resultat.errors?.forEach {
                            log.error("Error: {}", it.message)
                        }
                        log.error("Kunne ikke opprette ny sak for permitteringsmelding {}", grupperingsid)
                    }
                }
            } else {
                log.info("Opprettet ny sak {}", nySak.id)
            }
        }
        return
    }
}

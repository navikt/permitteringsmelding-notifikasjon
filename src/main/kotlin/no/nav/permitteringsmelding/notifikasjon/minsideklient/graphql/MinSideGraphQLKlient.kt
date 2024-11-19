package no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import no.nav.permitteringsmelding.notifikasjon.Env
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.permitteringsmelding.notifikasjon.basedOnEnv
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.ISO8601DateTime
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.OpprettNySak
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.inputs.AltinnMottakerInput
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.inputs.AltinnRessursMottakerInput
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.inputs.MottakerInput
import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.opprettnysak.*
import no.nav.permitteringsmelding.notifikasjon.log
import java.net.URL


private val defaultHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        jackson {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}

class MinSideGraphQLKlient(
    endpoint: String = Env.urlTilNotifikasjonIMiljo,
    httpClient: HttpClient = defaultHttpClient,
    private val oauth2Client: Oauth2Client
) {

    private val client = GraphQLKtorClient(url = URL(endpoint), httpClient = httpClient)

    private val mottaker = basedOnEnv(
        prod = { MottakerInput(altinn = AltinnMottakerInput(serviceCode = "5810", serviceEdition = "1"), altinnRessurs = null, naermesteLeder = null) },
        other = { MottakerInput(altinn = null, altinnRessurs = AltinnRessursMottakerInput(ressursId = "nav_permittering-og-nedbemmaning_innsyn-i-alle-innsendte-meldinger"), naermesteLeder = null) }
    )

    suspend fun opprettNySak(
        grupperingsid: String,
        merkelapp: String,
        virksomhetsnummer: String,
        tittel: String,
        lenke: String,
        tidspunkt: ISO8601DateTime? = null
    ) {
        val scopedAccessToken = oauth2Client.machine2machine().accessToken
        val resultat = client.execute(
            OpprettNySak(
                variables = OpprettNySak.Variables(
                    grupperingsid,
                    merkelapp,
                    virksomhetsnummer,
                    tittel,
                    lenke,
                    tidspunkt,
                    mottaker
                )
            )
        ) {
            header(HttpHeaders.Authorization, "Bearer $scopedAccessToken")
        }
        val nySak = resultat.data?.nySak

        if (nySak is NySakVellykket) {
            log.info("Opprettet ny sak {}", nySak.id)
        } else {
            when (nySak) {
                is DuplikatGrupperingsid -> log.info("Sak finnes allerede. hopper over. {}", nySak.feilmelding)
                is UgyldigMerkelapp -> throw Exception(nySak.feilmelding)
                is UgyldigMottaker -> throw Exception(nySak.feilmelding)
                is UkjentProdusent -> throw Exception(nySak.feilmelding)
                is UkjentRolle -> throw Exception(nySak.feilmelding)
                else -> {
                    throw Exception(resultat.errors?.joinToString { it.message })
                }
            }
        }
    }
}

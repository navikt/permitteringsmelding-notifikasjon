package no.nav.permitteringsmelding.notifikasjon.minsideklient

import no.nav.permitteringsmelding.notifikasjon.graphql.`generated"`.ISO8601DateTime
import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient

class MinSideNotifikasjonerService(private val minSideGraphQLClient : MinSideGraphQLKlient) {

    suspend fun opprettNySak(grupperingsid: String,
                             merkelapp: String,
                             virksomhetsnummer: String,
                             tittel: String,
                             lenke: String,
                             tidspunkt: ISO8601DateTime? = null) {
        minSideGraphQLClient.opprettNySak(
            grupperingsid,
            merkelapp,
            virksomhetsnummer,
            tittel,
            lenke,
            tidspunkt
        )
    }

    suspend fun slettSak(grupperingsid: String, merkelapp: String) {
        minSideGraphQLClient.slettSak(
            grupperingsid,
            merkelapp
        )
    }
}

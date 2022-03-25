package no.nav.permitteringsmelding.notifikasjon.minsideklient

import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient

class MinSideNotifikasjonerService(private val minSideGraphQLClient : MinSideGraphQLKlient) {

    suspend fun sendBeskjed(virksomhetsnummer: String,
                    lenke: String,
                    eksternId: String) {
        minSideGraphQLClient.opprettNySak(virksomhetsnummer, lenke, eksternId)
    }
}

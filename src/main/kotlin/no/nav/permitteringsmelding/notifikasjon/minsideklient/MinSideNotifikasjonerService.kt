package no.nav.permitteringsmelding.notifikasjon.minsideklient

import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient

class MinSideNotifikasjonerService(private val minSideGraphQLClient : MinSideGraphQLKlient) {

    fun sendBeskjed(virksomhetsnummer: String,
                    lenke: String,
                    eksternId: String) {
        minSideGraphQLClient.opprettNyBeskjed(virksomhetsnummer, lenke, eksternId)
    }
}

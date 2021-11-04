package no.nav.permitteringsvarsel.notifikasjon.minsideklient

import no.nav.permitteringsvarsel.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient

class MinSideNotifikasjonerService(private val minSideGraphQLClient : MinSideGraphQLKlient) {

    fun sendBeskjed(virksomhetsnummer: String,
                    lenke: String,
                    eksternId: String) {
        minSideGraphQLClient.opprettNyBeskjed(virksomhetsnummer, lenke, eksternId)
    }
}

package no.nav.permitteringsmelding.notifikasjon.setup

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.ktor.IssuerConfig

fun issuerConfig(mockOAuth2Server: MockOAuth2Server): IssuerConfig {
    val issuer = "default"
    val acceptedAudience = "default"

    return IssuerConfig(
        name = "arbeidsgiver",
        discoveryUrl = mockOAuth2Server.wellKnownUrl(issuer).toString(),
        acceptedAudience = listOf(acceptedAudience)
    )
}

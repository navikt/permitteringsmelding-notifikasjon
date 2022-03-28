package no.nav.permitteringsmelding.notifikasjon.setup

import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2Client
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse

class Oauth2ClientStub() : Oauth2Client {

    override suspend fun machine2machine(): OAuth2AccessTokenResponse {
        return OAuth2AccessTokenResponse("1234", 0, 0, emptyMap())
    }
}
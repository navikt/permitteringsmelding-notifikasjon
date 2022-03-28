package no.nav.permitteringsmelding.notifikasjon

import com.nimbusds.jwt.SignedJWT
import kotlinx.coroutines.runBlocking
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2ClientImpl
import no.nav.permitteringsmelding.notifikasjon.minsideklient.getDefaultHttpClient
import no.nav.permitteringsmelding.notifikasjon.setup.getAzureTestAuthProperties
import no.nav.permitteringsmelding.notifikasjon.setup.issuerConfig
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.junit.Test
import kotlin.test.assertTrue

class AuthenticationTest {

    companion object {
        private val azureAuthProperties = getAzureTestAuthProperties()
        private val mockOAuth2Server = MockOAuth2Server()

        init {
            mockOAuth2Server.shutdown()
            mockOAuth2Server.start()
        }
    }

    @Test
    fun skal_hente_machine_to_machine_token_med_azure() {
        startLokalApp(issuerConfig = issuerConfig(mockOAuth2Server)).use {

            val defaultHttpClient = getDefaultHttpClient()
            val oauth2Client = Oauth2ClientImpl(
                defaultHttpClient,
                azureAuthProperties
            )
            val nyAccessToken = runBlocking { oauth2Client.machine2machine(mockOAuth2Server.tokenEndpointUrl("issuer1").toString(),"aud2") }
            val jwt = SignedJWT.parse(nyAccessToken.accessToken)
            val claimsSet = jwt.jwtClaimsSet

            assertTrue(claimsSet.audience.contains("aud2"))
        }
    }
}
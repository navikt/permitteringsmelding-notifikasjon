package no.nav.permitteringsmelding.notifikasjon.autentisering

import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import no.nav.permitteringsmelding.notifikasjon.utils.environmentVariables
import no.nav.security.token.support.client.core.ClientAuthenticationProperties
import no.nav.security.token.support.client.core.OAuth2GrantType
import no.nav.security.token.support.client.core.OAuth2ParameterNames
import no.nav.security.token.support.client.core.auth.ClientAssertion
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import java.net.URI

interface Oauth2Client{
    suspend fun machine2machine(): OAuth2AccessTokenResponse
}

class Oauth2ClientImpl(
    private val httpClient: HttpClient,
    private val azureAuthProperties: ClientAuthenticationProperties) : Oauth2Client {

    suspend fun machine2machine(tokenEndpointUrl: String, scope: String) :OAuth2AccessTokenResponse {
        val grant = GrantRequest.machine2machine(scope)
        return httpClient.tokenRequest(
            tokenEndpointUrl,
            clientAuthProperties = azureAuthProperties,
            grantRequest = grant
        )
    }

    override suspend fun machine2machine(): OAuth2AccessTokenResponse {
        val tokenEndpointUrl = environmentVariables.azureADTokenEndpointUrl
        val scope = environmentVariables.notifikasjonerScope
        return machine2machine(tokenEndpointUrl, scope)
    }
}

data class GrantRequest(
    val grantType: OAuth2GrantType,
    val params: Map<String, String> = emptyMap()
) {
    companion object {
        fun machine2machine(scope: String): GrantRequest =
            GrantRequest(
                grantType = OAuth2GrantType.CLIENT_CREDENTIALS,
                params = mapOf(
                    OAuth2ParameterNames.SCOPE to scope
                )
            )
    }
}

internal suspend fun HttpClient.tokenRequest(
    tokenEndpointUrl: String,
    clientAuthProperties: ClientAuthenticationProperties,
    grantRequest: GrantRequest
): OAuth2AccessTokenResponse =
    submitForm(
        url = tokenEndpointUrl,
        formParameters = Parameters.build {
            appendClientAuthParams(
                tokenEndpointUrl = tokenEndpointUrl,
                clientAuthProperties = clientAuthProperties
            )
            append(OAuth2ParameterNames.GRANT_TYPE, grantRequest.grantType.value)
            grantRequest.params.forEach {
                append(it.key, it.value)
            }
        }
    )

private fun ParametersBuilder.appendClientAuthParams(
    tokenEndpointUrl: String,
    clientAuthProperties: ClientAuthenticationProperties
) = apply {
    val clientAssertion = ClientAssertion(URI.create(tokenEndpointUrl), clientAuthProperties)
    append(OAuth2ParameterNames.CLIENT_ID, clientAuthProperties.clientId)
    append(OAuth2ParameterNames.CLIENT_ASSERTION_TYPE, clientAssertion.assertionType())
    append(OAuth2ParameterNames.CLIENT_ASSERTION, clientAssertion.assertion())
}

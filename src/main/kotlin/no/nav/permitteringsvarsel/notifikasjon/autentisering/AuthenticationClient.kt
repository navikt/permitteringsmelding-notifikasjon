package no.nav.permitteringsvarsel.notifikasjon.autentisering


import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class AuthenticationClient(private val httpClient: HttpClient) {
    private val azureClientSecret = System.getenv("AZURE_APP_CLIENT_SECRET")!!
    private val azureClientId = System.getenv("AZURE_APP_CLIENT_ID")!!
    private val tokenEndpoint = System.getenv("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT")!!
    private val scope = System.getenv("MIN_SIDE_NOTIFIKASJONER_SCOPE")

    suspend fun getAccessToken(): AccessToken {
        val response = httpClient.submitForm<AccessToken>(
            url = tokenEndpoint,
            formParameters = Parameters.build {
                append("grant_type", "client_credentials")
                append("client_secret", azureClientSecret)
                append("client_id", azureClientId)
                append("scope", scope)
            }
        )
        return response
    }
}

data class AccessToken(
    val token_type: String,
    val expires_in: Int,
    val ext_expires_in: Int,
    val access_token: String
)

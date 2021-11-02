package no.nav.permitteringsvarsel.notifikasjon.autentisering

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result
import java.lang.RuntimeException

class AuthenticationClient(private val httpClient: FuelManager) {
    private val azureClientSecret = System.getenv("AZURE_APP_CLIENT_SECRET")!!
    private val azureClientId = System.getenv("AZURE_APP_CLIENT_ID")!!
    private val tokenEndpoint = System.getenv("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT")!!
    private val scope = System.getenv("MIN_SIDE_NOTIFIKASJONER_SCOPE")

    fun getAccessToken(): AccessToken {
        val formData = listOf(
            "grant_type" to "client_credentials",
            "client_secret" to azureClientSecret,
            "client_id" to azureClientId,
            "scope" to scope
        )

        val (_, _, result) = httpClient
            .post(tokenEndpoint, formData)
            .responseObject<AccessToken>()

        when(result) {
            is Result.Success -> return result.get()
            is Result.Failure -> throw RuntimeException("Kunde ikke hente token fra Azure Ad");
        }

    }
}

data class AccessToken(
    val token_type: String,
    val expires_in: Int,
    val ext_expires_in: Int,
    val access_token: String
)

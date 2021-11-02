package no.nav.permitteringsvarsel.notifikasjon.minsideklient

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

class MinSideNotifikasjonerKlient(private val httpClient: FuelManager) {
    fun sendNotifikasjon() {

        // Her skal det lages graphQL request



        val (request, response, result) = "https://httpbin.org/post"
            .httpPost()
            .responseString()
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println(ex)
            }
            is Result.Success -> {
                println("Ey yo")
            }
        }
    }
}

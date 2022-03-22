package no.nav.permitteringsmelding.notifikasjon.minsideklient

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.logging.*
import java.util.concurrent.TimeUnit

fun getHttpClient() : HttpClient {
    return HttpClient(engineFactory = OkHttp) {
        engine {
            config {
                connectTimeout(10, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
    }
}
package no.nav.permitteringsvarsel.notifikasjon

import no.nav.permitteringsvarsel.notifikasjon.utils.log
import io.javalin.Javalin
import utils.Liveness
import java.io.Closeable

// Her implementeres all businesslogikk
class App: Closeable {

    private val webServer = Javalin.create().apply {
        config.defaultContentType = "application/json"
        routes {
            get("/internal/isAlive") { if (Liveness.isAlive) it.status(200) else it.status(500) }
            get("/internal/isReady") { it.status(200) }
        }
    }


    fun start() {
        log.info("Starter app")
        webServer.start()
    }

    override fun close() {
        log.info("Stopper app")
    }
}

// Brukes når appen kjører på Nais
fun main() {
    App().start()
}

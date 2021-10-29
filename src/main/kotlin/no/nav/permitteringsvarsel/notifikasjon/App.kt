package no.nav.permitteringsvarsel.notifikasjon

import no.nav.permitteringsvarsel.notifikasjon.utils.log
import java.io.Closeable

// Her implementeres all businesslogikk
class App: Closeable {

    fun start() {
        log.info("Starter app")
    }

    override fun close() {
        log.info("Stopper app")
    }
}

// Brukes når appen kjører på Nais
fun main() {
    App().start()
}

package no.nav.permitteringsvarsel.notifikasjon

import java.io.Closeable

// Her implementeres all businesslogikk
class App: Closeable {

    fun start() {
        println("Starter app")
    }

    override fun close() {
        println("Stopper app")
    }
}

// Brukes når appen kjører på Nais
fun main() {
    App().start()
}

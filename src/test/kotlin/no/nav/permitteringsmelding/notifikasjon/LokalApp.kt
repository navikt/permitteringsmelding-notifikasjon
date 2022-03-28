package no.nav.permitteringsmelding.notifikasjon

import no.nav.permitteringsmelding.kafka.PermitteringsmeldingConsumer
import no.nav.permitteringsmelding.notifikasjon.setup.issuerConfig
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.ktor.IssuerConfig

// Brukes for å kjøre appen i tester
fun startLokalApp(permitteringsmeldingConsumer: PermitteringsmeldingConsumer): App {

    val app = App(permitteringsmeldingConsumer)
    app.start()
    return app
}

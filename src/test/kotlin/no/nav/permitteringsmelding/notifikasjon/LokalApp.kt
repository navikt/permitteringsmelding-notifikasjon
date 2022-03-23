package no.nav.permitteringsmelding.notifikasjon

import no.nav.permitteringsmelding.notifikasjon.setup.issuerConfig
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.ktor.IssuerConfig

// Brukes for å kjøre appen i tester
fun startLokalApp(issuerConfig: IssuerConfig = issuerConfig(MockOAuth2Server())): App {
    val app = App()
    app.start()
    return app
}

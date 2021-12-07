package no.nav.permitteringsvarsel.notifikasjon

import no.nav.permitteringsvarsel.notifikasjon.database.Database
import org.junit.Test


class DatabaseOppsettTest {
    @Test
    fun `Test for å sjekke oppsett av database`() {
        val database = Database()
        database.runQuery()
    }
}
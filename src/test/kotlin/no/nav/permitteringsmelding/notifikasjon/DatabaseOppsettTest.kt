package no.nav.permitteringsmelding.notifikasjon

import no.nav.permitteringsmelding.notifikasjon.database.Database
import org.junit.Test


class DatabaseOppsettTest {
    @Test
    fun `Test for å sjekke oppsett av database`() {
        val database = Database()
        database.runQuery()
    }
}
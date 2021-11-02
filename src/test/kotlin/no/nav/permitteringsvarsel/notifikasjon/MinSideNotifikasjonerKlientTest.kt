package no.nav.permitteringsvarsel.notifikasjon

import com.github.kittinunf.fuel.core.FuelManager
import no.nav.permitteringsvarsel.notifikasjon.minsideklient.MinSideNotifikasjonerKlient
import org.junit.Test

class MinSideNotifikasjonerKlientTest {

    @Test
    fun `Skal sende nofifikasjon til Min Side notifikasjoner api`() {
        val minSideNotifikasjonerKlient = MinSideNotifikasjonerKlient(FuelManager())
        minSideNotifikasjonerKlient.sendNotifikasjon()
        println("ohoy")
    }

    @Test
    fun `Skal parse en melding og transformere til en notifikasjon med GraphQL`() {
        // En dataklass med json som er meldingen fra kafka

        // Kode som parser denne til GraphQL

        // Sjekke at det blir ok

    }
}
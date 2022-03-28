package no.nav.permitteringsmelding.notifikasjon.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.permitteringsmelding.notifikasjon.minsideklient.PermitteringsMelding
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class MapperTest {

    @Test
    fun `Test for å parse melding som string`() {
        val jsonString: String = File("permitteringsMelding.json").readText(Charsets.UTF_8)
        val mapper = jacksonObjectMapper()
        val permitteringsmelding: PermitteringsMelding = mapper.readValue(jsonString)
        assertEquals(permitteringsmelding.bedriftsnummer, "910825526")
    }

}
package no.nav.permitteringsmelding.notifikasjon.kafka

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Test
import kotlin.test.assertEquals

class MapperTest {
    @Test
    fun `Test for å parse melding som string`() {
        val permitteringsmelding: PermitteringsMelding = kafkaObjectMapper.readValue(javaClass.getResource("/json/permitteringsMelding.json")!!)
        val meldingType = MeldingType.valueOf(permitteringsmelding.type)

        assertEquals(permitteringsmelding.bedriftsnummer, "910825526")
        assertEquals(meldingType.merkelapp, "Nedbemanning")
    }

}
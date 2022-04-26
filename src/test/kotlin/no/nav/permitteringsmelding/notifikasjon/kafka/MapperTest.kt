package no.nav.permitteringsmelding.notifikasjon.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.permitteringsmelding.notifikasjon.kafka.PermitteringsMelding
import org.junit.Test
import kotlin.test.assertEquals

class MapperTest {
    val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val json = """
      {     
  "id": "5f6027bf-0604-4acf-8168-0c1611f33c0d",
  "bedriftsnummer": 910825526,
  "bedriftNavn": "BIFF OG BULJONG",
  "sendtInnTidspunkt": "2020-03-18T10:19:30.000Z",
  "type": "MASSEOPPSIGELSE",
  "kontaktNavn": "Tore Toresen",
  "kontaktTlf": "66778899",
  "kontaktEpost": "tore.toresen@example.com",
  "varsletAnsattDato": "2020-03-14",
  "varsletNavDato": "2020-03-13",
  "startDato": "2020-03-16",
  "sluttDato": "2020-09-20",
  "fritekst": "Lorem ipsum, dorem dimsum",
  "antallBerorte": 123,
  "ukjent": [5,6,7]
      }"""

    @Test
    fun `Test for å parse melding som string`() {
        val permitteringsmelding: PermitteringsMelding = mapper.readValue(json)
        val meldingType = MeldingType.valueOf(permitteringsmelding.type)

        assertEquals(permitteringsmelding.bedriftsnummer, "910825526")
        assertEquals(meldingType.merkelapp, "Nedbemanning")
    }

}
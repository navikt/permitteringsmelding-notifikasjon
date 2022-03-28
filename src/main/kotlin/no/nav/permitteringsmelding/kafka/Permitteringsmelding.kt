package no.nav.permitteringsmelding.kafka

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class PermitteringsMelding {
    private val id: UUID? = null
    private val bedriftsnummer: String? = null
    private val bedriftNavn: String? = null

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private val sendtInnTidspunkt: LocalDateTime? = null
    private val type: String? = null
    private val kontaktNavn: String? = null
    private val kontaktTlf: String? = null
    private val kontaktEpost: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    private val varsletAnsattDato: LocalDate? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    private val varsletNavDato: LocalDate? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    private val startDato: LocalDate? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    private val sluttDato: LocalDate? = null
    private val fritekst: String? = null
    private val antallBerorte = 0
    private val kommuneNummer: String? = null
    private val behandlendeEnhet: String? = null
}
package no.nav.permitteringsmelding.kafka

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class PermitteringsMelding (
    val id: String? = null,
    val bedriftsnummer: String? = null,
    val bedriftNavn: String? = null,
    val sendtInnTidspunkt: String? = null,
    val type: String? = null,
    val kontaktNavn: String? = null,
    val kontaktTlf: String? = null,
    val kontaktEpost: String? = null,

    val varsletAnsattDato: String? = null,

    val varsletNavDato: String? = null,

    val startDato: String? = null,

    val sluttDato: String? = null,
    val fritekst: String? = null,
    val antallBerorte: Number? = 0
)
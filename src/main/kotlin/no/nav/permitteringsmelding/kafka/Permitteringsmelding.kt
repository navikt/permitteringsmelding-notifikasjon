package no.nav.permitteringsmelding.kafka

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class PermitteringsMelding (
    val id: String,
    val bedriftsnummer: String,
    val sendtInnTidspunkt: String,
    val type: String
)
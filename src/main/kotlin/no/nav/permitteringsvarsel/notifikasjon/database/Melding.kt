package no.nav.permitteringsvarsel.notifikasjon.database

import java.time.LocalDate
import java.util.*

data class Melding(
    val id: UUID,
    val opprettetAv: String,
    val bedriftsNummer: String,
    val varsletNavDato: LocalDate,
    val startDato: LocalDate,
    val sluttDato: LocalDate?,
) {
}
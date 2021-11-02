package no.nav.permitteringsvarsel.notifikasjon.minsideklient

import java.time.LocalDate
import java.util.*

data class PermitteringsMelding(
    val id: UUID,
    val bedriftsnummer: String,
    val fnr: String,
    val sendtInnTidspunkt: Date,
    val varsletAnsattDato: LocalDate?,
    val varsletNavDato: LocalDate?,
    val startDato: LocalDate?
)

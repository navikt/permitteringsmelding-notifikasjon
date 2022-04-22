package no.nav.permitteringsmelding.notifikasjon.kafka

data class PermitteringsMelding (
    val id: String,
    val bedriftsnummer: String,
    val sendtInnTidspunkt: String,
    val type: String
)
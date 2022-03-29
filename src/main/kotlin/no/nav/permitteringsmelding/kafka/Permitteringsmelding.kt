package no.nav.permitteringsmelding.kafka

data class PermitteringsMelding (
    val id: String,
    val bedriftsnummer: String,
    val sendtInnTidspunkt: String,
    val type: String
)
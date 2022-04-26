package no.nav.permitteringsmelding.notifikasjon.kafka

enum class MeldingType(val merkelapp: String, val tittel: String) {
    MASSEOPPSIGELSE("Nedbemanning", "Melding om oppsigelse"),
    PERMITTERING_UTEN_LØNN("Permittering", "Melding om permittering"),
    INNSKRENKNING_I_ARBEIDSTID("Innskrenking av arbeidstid", "Melding om innskrenking av arbeidstid")
}

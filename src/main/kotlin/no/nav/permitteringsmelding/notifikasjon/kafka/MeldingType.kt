package no.nav.permitteringsmelding.notifikasjon.kafka

enum class MeldingType(val merkelapp: String, val tittel: String) {
    MASSEOPPSIGELSE("Masseoppsigelse", "Masseoppsigelse"),
    PERMITTERING_UTEN_LØNN("Permittering", "Permittering"),
    INNSKRENKNING_I_ARBEIDSTID("Innskrenkning i arbeidstid", "Innskrenkning i arbeidstid")
}

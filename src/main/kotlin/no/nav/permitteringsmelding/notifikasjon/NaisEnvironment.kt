package no.nav.permitteringsmelding.notifikasjon

object NaisEnvironment {
    val clusterName: String = System.getenv("NAIS_CLUSTER_NAME") ?: ""
}
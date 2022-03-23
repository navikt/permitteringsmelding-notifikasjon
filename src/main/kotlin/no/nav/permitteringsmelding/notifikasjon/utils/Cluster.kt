package no.nav.permitteringsmelding.notifikasjon.utils

enum class Cluster {
    DEV_GCP, PROD_GCP;

    companion object {
        val current: Cluster by lazy {
            when (val c = System.getenv("NAIS_CLUSTER_NAME")) {
                "dev-gcp" -> DEV_GCP
                "prod-gcp" -> PROD_GCP
                else -> throw RuntimeException("Ukjent cluster: $c, må kjøre på Nais. Se LokalApp.kt for kjøring lokalt.")
            }
        }
    }
}

package no.nav.permitteringsmelding.notifikasjon


object Env {
    val azureADTokenEndpointUrl = System.getenv("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT") ?: "http://localhost:8080"

    val azureClientId = System.getenv("AZURE_APP_CLIENT_ID")
    val azureJWK = System.getenv("AZURE_APP_JWK")

    val urlTilNotifikasjonIMiljo = basedOnEnv(
        prod = { "http://notifikasjon-produsent-api.fager/api/graphql" },
        dev = { "http://notifikasjon-produsent-api.fager/api/graphql" },
        other = { "http://localhost:8080" },
    )

    val notifikasjonerScope = basedOnEnv(
            prod = { "api://prod-gcp.fager.notifikasjon-produsent-api/.default" },
            dev = { "api://dev-gcp.fager.notifikasjon-produsent-api/.default" },
            other = { "lokal" },
        )

    val urlTilPermitteringsløsningFrontend = basedOnEnv(
        prod = { "https://arbeidsgiver.nav.no/permittering/skjema/kvitteringsside/" },
        dev = { "https://permitteringsskjema.intern.dev.nav.no/permittering/skjema/kvitteringsside/" },
        other = { "http://localhost:8080" },
    )
}
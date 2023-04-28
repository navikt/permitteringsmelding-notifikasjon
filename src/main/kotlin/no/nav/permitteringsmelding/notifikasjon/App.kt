package no.nav.permitteringsmelding.notifikasjon

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import kotlinx.coroutines.*
import no.nav.permitteringsmelding.notifikasjon.autentisering.Oauth2ClientImpl
import no.nav.permitteringsmelding.notifikasjon.kafka.*
import no.nav.permitteringsmelding.notifikasjon.minsideklient.graphql.MinSideGraphQLKlient
import no.nav.security.token.support.client.core.ClientAuthenticationProperties
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

private val metricsDispatcher: CoroutineContext = Executors.newFixedThreadPool(1)
    .produceMetrics("internal-http")
    .asCoroutineDispatcher()


object App {
    fun main(
        httpPort: Int,
        consumer: CoroutineKafkaConsumer<String, PermitteringsMelding>,
        minsideraphQLKlient: MinSideGraphQLKlient,
    ) {
        runBlocking(Dispatchers.Default) {

            launch {
                consumer.forEach { record ->
                    val permitteringsMelding = record.value()
                    val meldingType = MeldingType.valueOf(permitteringsMelding.type)
                    minsideraphQLKlient.opprettNySak(
                        grupperingsid = permitteringsMelding.id,
                        merkelapp = meldingType.merkelapp,
                        virksomhetsnummer = permitteringsMelding.bedriftsnummer,
                        tittel = meldingType.tittel,
                        lenke = "${Env.urlTilPermitteringsløsningFrontend}${permitteringsMelding.id}",
                        tidspunkt = permitteringsMelding.sendtInnTidspunkt
                    )
                }
            }

            launch {
                embeddedServer(Netty, port = httpPort) {
                    install(MicrometerMetrics) {
                        registry = Metrics.meterRegistry
                        distributionStatisticConfig = DistributionStatisticConfig.Builder()
                            .percentilesHistogram(true)
                            .build()
                        meterBinders = listOf(
                            ClassLoaderMetrics(),
                            JvmMemoryMetrics(),
                            JvmGcMetrics(),
                            ProcessorMetrics(),
                            JvmThreadMetrics(),
                            LogbackMetrics()
                        )
                    }
                    routing {
                        route("internal") {
                            get("alive") {
                                if (Health.alive) {
                                    call.respond(HttpStatusCode.OK)
                                } else {
                                    call.respond(
                                        HttpStatusCode.ServiceUnavailable,
                                        Health.subsystemAlive.toString()
                                    )
                                }
                            }
                            get("ready") {
                                if (Health.ready) {
                                    call.respond(HttpStatusCode.OK)
                                } else {
                                    call.respond(
                                        HttpStatusCode.ServiceUnavailable,
                                        Health.subsystemReady.toString()
                                    )
                                }
                            }
                            get("metrics") {
                                withContext(coroutineContext + metricsDispatcher) {
                                    call.respondText(Metrics.meterRegistry.scrape())
                                }
                            }
                        }
                    }
                }.start(wait = true)
            }
        }
    }
}

fun main() {
    App.main(
        httpPort = 8080,
        consumer = CoroutineKafkaConsumer.new(
            topic = permitteringsmeldingtopic,
            groupId = "permitteringsmelding-notifikasjon1",
            keyDeserializer = StringDeserializer::class.java,
            valueDeserializer = PermitteringsMeldingDeserializer::class.java,
            configure = { },
        ),
        minsideraphQLKlient = MinSideGraphQLKlient(
            oauth2Client = Oauth2ClientImpl(
                ClientAuthenticationProperties.builder()
                    .clientId(Env.azureClientId)
                    .clientJwk(Env.azureJWK)
                    .clientAuthMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                    .build()
            )
        ),
    )
}


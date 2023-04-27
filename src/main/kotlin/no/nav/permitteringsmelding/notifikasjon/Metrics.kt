package no.nav.permitteringsmelding.notifikasjon

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry
import java.util.concurrent.ExecutorService

object Metrics {
    val clock: Clock = Clock.SYSTEM

    val meterRegistry = PrometheusMeterRegistry(
        PrometheusConfig.DEFAULT,
        CollectorRegistry.defaultRegistry,
        clock
    )
}

fun <T: ExecutorService> T.produceMetrics(name: String): T {
    ExecutorServiceMetrics(this, name, emptyList())
        .bindTo(Metrics.meterRegistry)
    return this
}

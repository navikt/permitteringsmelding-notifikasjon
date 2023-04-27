package no.nav.permitteringsmelding.notifikasjon

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.pow

fun Int.toThePowerOf(exponent: Int): Long = toDouble().pow(exponent).toLong()

fun <T> basedOnEnv(
    other: () -> T,
    prod: () -> T = other,
    dev: () -> T = other,
): T =
    when (NaisEnvironment.clusterName) {
        "prod-gcp" -> prod()
        "dev-gcp" -> dev()
        else -> other()
    }


val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)
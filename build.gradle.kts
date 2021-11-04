import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.graphql

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("com.expediagroup.graphql") version "5.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    application
}

repositories {
    gradlePluginPortal()
    maven("https://kotlin.bintray.com/kotlinx")
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("io.javalin:javalin:4.1.1")

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.expediagroup:graphql-kotlin-ktor-client:5.2.0")
    val ktor_version = "1.6.4"
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")
    implementation("com.graphql-java:graphql-java:16.2")
    val fuelVersion = "2.3.1"
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-jackson:$fuelVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
}

val graphqlGenerateClient by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask::class) {
    packageName.set("no.nav.permitteringsvarsel.notifikasjon.graphql.generated\"")
    schemaFile.set(file("src/main/resources/schema.graphql"))
    serializer.set(GraphQLSerializer.KOTLINX)
}


application {
    mainClass.set("no.nav.permitteringsvarsel.notifikasjon.AppKt")
}

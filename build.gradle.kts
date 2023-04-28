import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("com.expediagroup.graphql") version "5.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    application
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    gradlePluginPortal()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.expediagroup:graphql-kotlin-ktor-client:5.2.0")
    val ktor_version = "1.6.4"
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:1.9.4")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")
    implementation("com.graphql-java:graphql-java:16.2")
    implementation("no.nav.security:token-validation-ktor:1.3.10")
    implementation("no.nav.security:token-client-core:1.3.10")

    implementation("org.apache.kafka:kafka-clients:2.7.0")
    implementation("io.confluent:kafka-avro-serializer:6.0.1")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")

    testImplementation("no.nav.security:mock-oauth2-server:0.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation("io.mockk:mockk:1.12.2")
}

val graphqlGenerateClient by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask::class) {
    packageName.set("no.nav.permitteringsmelding.notifikasjon.graphql.generated\"")
    schemaFile.set(file("src/main/resources/schema.graphql"))
    serializer.set(GraphQLSerializer.KOTLINX)
}

application {
    mainClass.set("no.nav.permitteringsmelding.notifikasjon.AppKt")
}

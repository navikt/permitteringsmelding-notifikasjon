import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("com.expediagroup.graphql") version "5.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.expediagroup:graphql-kotlin-ktor-client:7.0.2")
    val ktorVersion = "2.3.6"
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:1.9.4")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")
    implementation("com.graphql-java:graphql-java:21.3")
    implementation("no.nav.security:token-validation-ktor:3.2.0")
    implementation("no.nav.security:token-client-core:1.3.10")

    implementation("org.apache.kafka:kafka-clients:3.6.1")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")

    testImplementation("no.nav.security:mock-oauth2-server:0.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.mockk:mockk:1.12.2")
}

val graphqlGenerateClient by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask::class) {
    packageName.set("no.nav.permitteringsmelding.notifikasjon.graphql.generated\"")
    schemaFile.set(file("src/main/resources/schema.graphql"))
    serializer.set(GraphQLSerializer.KOTLINX)
}

val mainClass = "no.nav.permitteringsmelding.notifikasjon.AppKt"

tasks.jar {
    destinationDirectory.set(file("$buildDir/"))
    manifest {
        attributes["Main-Class"] = mainClass
        attributes["Class-Path"] = configurations.runtimeClasspath.get()
            .joinToString(separator = " ") { "libs/${it.name}" }
    }
}

tasks.assemble {
    configurations.runtimeClasspath.get().forEach {
        val file = File("$buildDir/libs/${it.name}")
        if (!file.exists())
            it.copyTo(file)
    }
}

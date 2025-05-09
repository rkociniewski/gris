import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "rk.softblue"
version = "1.0-SNAPSHOT"
val javaVersion = JavaVersion.VERSION_21

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.dokka)
    alias(libs.plugins.manes)
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

application {
    mainClass.set("rk.softblue.recruitment.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.jackson.module)
    implementation(libs.jackson.datatype)

    implementation(libs.koin.ktor)
    implementation(libs.koin.test)
    implementation(libs.koin.logger)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.serialization.kotlinx)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.negotiation)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status)

    implementation(libs.logback)
    runtimeOnly(libs.kotlin.logging)

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test-junit5"))
}


testlogger {
    showStackTraces = false
    showFullStackTraces = false
    showCauses = false
    slowThreshold = 10000
    showSimpleNames = true
}

tasks.test {
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    useJUnitPlatform()
}
tasks {
    // dokka configuration
    dokkaHtml {
        outputDirectory.set(layout.buildDirectory.dir("dokka")) // output directory of dokka documentation.
        // source set configuration.
        dokkaSourceSets {
            named("main") { // source set name.
                jdkVersion.set(java.targetCompatibility.toString().toInt()) // Used for linking to JDK documentation
                skipDeprecated.set(false) // Add output to deprecated members. Applies globally, can be overridden by packageOptions
                includeNonPublic.set(true) // non-public modifiers should be documented
            }
        }
    }

    test {
        useJUnitPlatform()
    }
}

kotlin {
    compilerOptions {
        verbose = true // enable verbose logging output
        jvmTarget.set(JvmTarget.fromTarget(java.targetCompatibility.toString())) // target version of the generated JVM bytecode
    }
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    // Ogranicz tylko do stabilnych wersji
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

fun isNonStable(version: String): Boolean {
    return listOf("alpha", "beta", "rc", "cr", "m", "preview", "snapshot", "dev")
        .any { version.lowercase().contains(it) }
}
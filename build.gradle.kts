import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * artifact group
 */
group = "rk.powermilk"

/**
 * project version
 */
version = "1.1.10"

val javaVersion = JavaVersion.VERSION_21
val jvmTargetVersion = JvmTarget.JVM_21.target

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    jacoco
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

application {
    mainClass.set("rk.powermilk.gris.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

// dependencies
dependencies {
    detektPlugins(libs.detekt)

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
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.cors)

    implementation(libs.swagger)

    implementation(libs.logback)
    implementation(libs.kotlin.logging)

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

kotlin {
    compilerOptions {
        verbose = true // enable verbose logging output
        jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion)) // target version of the generated JVM bytecode
    }
}

detekt {
    source.setFrom("src/main/kotlin")
    config.setFrom("$projectDir/detekt.yml")
    autoCorrect = true
}

dokka {
    dokkaSourceSets.main {
        jdkVersion.set(java.targetCompatibility.toString().toInt()) // Used for linking to JDK documentation
        skipDeprecated.set(false)
    }

    pluginsConfiguration.html {
        dokkaSourceSets {
            configureEach {
                documentedVisibilities.set(
                    setOf(
                        VisibilityModifier.Public,
                        VisibilityModifier.Private,
                        VisibilityModifier.Protected,
                        VisibilityModifier.Internal,
                        VisibilityModifier.Package,
                    )
                )
            }
        }
    }
}

tasks.test {
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/ApplicationKt.class",
                        "**/ApplicationKt\$*.class",
                        "**/KoinKt.class",
                        "**/KoinKt\$*.class",
                        "**/*\$logger\$*.class",
                        "**/*\$Companion.class",
                        // Dodaj też inne config klasy z logger lambdami
                        "**/LoggingKt\$*.class",
                        "**/SwaggerKt\$*.class",
                        "**/GitHubControllerKt\$*.class",
                        "**/GitHubServiceImpl\$*.class",
                    )
                }
            }
        )
    )
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)

    val excludesList = listOf(
        "rk.powermilk.gris.ApplicationKt*",
        "rk.powermilk.gris.config.KoinKt*",
        $$"*.config.*Kt$logger*",
        $$"*.controller.*Kt$logger*",
        $$"*.service.*Kt$logger*"
    )

    violationRules {
        rule {
            excludes = excludesList
            limit {
                minimum = "0.75".toBigDecimal()
            }
        }
        rule {
            enabled = true
            element = "CLASS"
            includes = listOf("rk.*")
            excludes = excludesList
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
        }
    }
}

tasks.register("cleanReports") {
    doLast {
        delete("${layout.buildDirectory}/reports")
    }
}

tasks.register("coverage") {
    dependsOn(tasks.test, tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = jvmTargetVersion
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = jvmTargetVersion
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget


group = "rk.softblue"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

val dokkaVersion: String by project
val jacksonVersion: String by project
val junitVersion: String by project
val koinVersion: String by project
val kotlinLoggingVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
val mockkVersion: String by project
val testLoggerVersion: String by project
val stoveVersion: String by project

plugins {
    id("com.adarshr.test-logger")
    id("org.jetbrains.dokka")
    id("io.ktor.plugin")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

application {
    mainClass.set("rk.softblue.recruitment.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("io.insert-koin:koin-ktor3:$koinVersion")
    implementation("io.insert-koin:koin-test:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    runtimeOnly("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
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
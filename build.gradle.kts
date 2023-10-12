group = "rk.softblue"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

val dokkaVersion: String by project
val jacksonVersion: String by project
val junitVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
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
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
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
    // compile kotlin configuration
    compileKotlin {
        kotlinOptions {
            allWarningsAsErrors = true // report an error if there are any warnings
            verbose = true // enable verbose logging output
            jvmTarget = java.targetCompatibility.toString() // target version of the generated JVM bytecode
            freeCompilerArgs = listOf("-Xjsr305=strict") // list of additional compiler arguments
        }
    }

    // compile kotlin tests configuration
    compileTestKotlin {
        kotlinOptions {
            verbose = true // enable verbose logging output
            jvmTarget = java.targetCompatibility.toString() // target version of the generated JVM bytecode
            freeCompilerArgs = listOf("-Xjsr305=strict") // list of additional compiler arguments
        }
    }

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
    jvmToolchain(17)
}
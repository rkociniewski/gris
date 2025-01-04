package rk.softblue.recruitment

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.plugin.Koin
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.config.errorHandling
import rk.softblue.recruitment.controller.configureGHRouting
import rk.softblue.recruitment.di.appModule

fun main() {
    run()
}

fun run() = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)

fun Application.module() {
    configureKoin()
    configureGHRouting()
    configureSerialization()
    configureMonitoring()
    errorHandling()
}

fun Application.configureKoin() {
    install(Koin) {
        // Adding modules
        modules(appModule)
    }
}
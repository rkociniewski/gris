package rk.softblue.recruitment

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import rk.softblue.recruitment.config.configureErrorHandling
import rk.softblue.recruitment.config.configureKoin
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.config.configureSwagger
import rk.softblue.recruitment.controller.configureGHRouting

fun main() {
    run()
}

fun run() = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)

fun Application.module() {
    configureKoin()
    configureGHRouting()
    configureSerialization()
    configureMonitoring()
    configureErrorHandling()
    configureSwagger()
}

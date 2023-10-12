package rk.softblue.recruitment

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.config.errorHandling
import rk.softblue.recruitment.controller.configureRouting
import rk.softblue.recruitment.service.RKGithubService

fun main() {
    run()
}

fun run() = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)

fun Application.module() {
    configureRouting(RKGithubService())
    configureSerialization()
    configureMonitoring()
    errorHandling()
}
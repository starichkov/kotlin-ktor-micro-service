package com.templatetasks.kotlin.ktor

import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.templatetasks.kotlin.ktor.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::myApplicationModule).start(wait = true)
}

fun Application.myApplicationModule() {
    configureRouting()
    configureSerialization()
    configureAdministration()
}

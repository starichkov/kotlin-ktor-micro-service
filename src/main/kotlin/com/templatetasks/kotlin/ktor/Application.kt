package com.templatetasks.kotlin.ktor

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.templatetasks.kotlin.ktor.plugins.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureAdministration()
    }.start(wait = true)
}

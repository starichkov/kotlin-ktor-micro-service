package com.templatetasks.kotlin.ktor

import com.templatetasks.kotlin.ktor.dao.DatabaseFactory
import com.templatetasks.kotlin.ktor.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 2022-05-08 15:00
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()
        configureRouting()
        configureSerialization()
        configureAdministration()
    }.start(wait = true)
}

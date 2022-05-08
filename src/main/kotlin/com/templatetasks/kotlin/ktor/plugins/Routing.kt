package com.templatetasks.kotlin.ktor.plugins

import com.templatetasks.kotlin.ktor.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        customerRouting()
        orderRouting()
    }
}

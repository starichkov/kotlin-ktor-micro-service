package com.templatetasks.kotlin.ktor.plugins

import com.templatetasks.kotlin.ktor.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 2022-05-08 15:00
 */
fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        customerRouting()
        orderRouting()
    }
}

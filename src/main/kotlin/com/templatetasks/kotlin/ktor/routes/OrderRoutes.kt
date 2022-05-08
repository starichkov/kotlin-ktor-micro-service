package com.templatetasks.kotlin.ktor.routes

import com.templatetasks.kotlin.ktor.models.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRouting() {
    route("/order") {

        get {
            if (orderStorage.isNotEmpty()) {
                call.respond(orderStorage)
            }
        }

        get("{number?}") {
            val number =
                call.parameters["number"] ?: return@get call.respondText(
                    "Bad Request",
                    status = HttpStatusCode.BadRequest
                )
            val order = orderStorage.find { it.number == number } ?: return@get call.respondText(
                "Not Found",
                status = HttpStatusCode.NotFound
            )
            call.respond(order)
        }

        get("{number?}/total") {
            val number =
                call.parameters["number"] ?: return@get call.respondText(
                    "Bad Request",
                    status = HttpStatusCode.BadRequest
                )
            val order = orderStorage.find { it.number == number } ?: return@get call.respondText(
                "Not Found",
                status = HttpStatusCode.NotFound
            )
            val total = order.contents.sumOf { it.price * it.amount }
            call.respond(total)
        }
    }
}
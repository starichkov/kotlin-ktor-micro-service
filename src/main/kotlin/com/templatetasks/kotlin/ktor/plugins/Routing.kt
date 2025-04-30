package com.templatetasks.kotlin.ktor.plugins

import com.templatetasks.kotlin.ktor.routes.*
import com.templatetasks.kotlin.ktor.service.CustomerService
import com.templatetasks.kotlin.ktor.service.OrderService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val customerService = CustomerService()
    val orderService = OrderService()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        customerRouting(customerService)
        orderRouting(orderService)
    }
}

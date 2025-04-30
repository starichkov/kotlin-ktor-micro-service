package com.templatetasks.kotlin.ktor.routes

import com.templatetasks.kotlin.ktor.api.handleResult
import com.templatetasks.kotlin.ktor.api.respondError
import com.templatetasks.kotlin.ktor.models.Order
import com.templatetasks.kotlin.ktor.service.OrderService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.orderRouting(orderService: OrderService) {
    route("/order") {

        get {
            val result = orderService.getAllOrders()
            call.handleResult(result)
        }

        get("{number?}") {
            val number = call.parameters["number"]
            if (number.isNullOrBlank()) {
                call.respondError(
                    HttpStatusCode.BadRequest,
                    "Bad Request",
                    "Order number cannot be blank"
                )
                return@get
            }
            val result = orderService.getOrder(number)
            call.handleResult(result)
        }

        get("{number?}/total") {
            val number = call.parameters["number"]
            if (number.isNullOrBlank()) {
                call.respondError(
                    HttpStatusCode.BadRequest,
                    "Bad Request",
                    "Order number cannot be blank"
                )
                return@get
            }
            val result = orderService.getOrderTotal(number)
            call.handleResult(result)
        }

        post {
            try {
                val order = call.receive<Order>()
                val result = orderService.createOrder(order)
                call.handleResult(result)
            } catch (e: ContentTransformationException) {
                call.respondError(
                    HttpStatusCode.BadRequest,
                    "Bad Request",
                    "Invalid order data format"
                )
            }
        }

        delete("{number?}") {
            val number = call.parameters["number"]
            if (number.isNullOrBlank()) {
                call.respondError(
                    HttpStatusCode.BadRequest,
                    "Bad Request",
                    "Order number cannot be blank"
                )
                return@delete
            }
            val result = orderService.deleteOrder(number)
            call.handleResult(result)
        }
    }
}

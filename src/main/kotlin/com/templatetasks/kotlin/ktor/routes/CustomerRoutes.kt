package com.templatetasks.kotlin.ktor.routes

import com.templatetasks.kotlin.ktor.api.handleResult
import com.templatetasks.kotlin.ktor.api.respondError
import com.templatetasks.kotlin.ktor.models.Customer
import com.templatetasks.kotlin.ktor.service.CustomerService
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.customerRouting(customerService: CustomerService) {
    route("/customer") {

        get {
            val result = customerService.getAllCustomers()
            call.handleResult(result)
        }

        get("{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respondError(
                    HttpStatusCode.BadRequest,
                    "Bad Request",
                    "Customer ID cannot be blank"
                )
                return@get
            }
            val result = customerService.getCustomer(id)
            call.handleResult(result)
        }

        post {
            try {
                val customer = call.receive<Customer>()
                val result = customerService.createCustomer(customer)
                call.handleResult(result)
            } catch (e: Exception) {
                when (e) {
                    is BadRequestException, is io.ktor.server.request.ContentTransformationException -> {
                        call.respondError(
                            HttpStatusCode.BadRequest,
                            "Bad Request",
                            "Invalid customer data format"
                        )
                    }

                    else -> throw e
                }
            }
        }

        delete("{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respondError(
                    HttpStatusCode.BadRequest,
                    "Bad Request",
                    "Customer ID cannot be blank"
                )
                return@delete
            }
            val result = customerService.deleteCustomer(id)
            call.handleResult(result)
        }
    }
}

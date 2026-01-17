package com.templatetasks.kotlin.ktor

import com.templatetasks.kotlin.ktor.api.ApiResult
import com.templatetasks.kotlin.ktor.models.Order
import com.templatetasks.kotlin.ktor.models.OrderItem
import com.templatetasks.kotlin.ktor.plugins.configureRouting
import com.templatetasks.kotlin.ktor.plugins.configureSerialization
import com.templatetasks.kotlin.ktor.routes.orderRouting
import com.templatetasks.kotlin.ktor.service.OrderService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.*

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 08.05.2022 16:30
 */
class OrderRoutesTest {

    @Test
    fun testGetOrders() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetOrder() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/2022-05-01-01")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("2022-05-01-01"))
        assertTrue(response.bodyAsText().contains("Ham Sandwich"))
    }

    @Test
    fun testGetOrderTotal() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/2022-05-01-01/total")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("23.15", response.bodyAsText())
    }

    @Test
    fun testGetNonExistentOrderTotal() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/non-existent/total")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testCreateDuplicateOrder() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val number = "duplicate-${java.util.UUID.randomUUID()}"
        val order = Order(number, listOf(OrderItem("Item 1", 1, 10.0)))
        // First creation
        val response1 = client.post("/order") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(order))
        }
        assertEquals(HttpStatusCode.OK, response1.status)

        // Duplicate creation
        val response2 = client.post("/order") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(order))
        }
        assertEquals(HttpStatusCode.BadRequest, response2.status)
        assertTrue(response2.bodyAsText().contains("Order with number $number already exists"))
    }

    @Test
    fun testGetOrderWithBlankNumber() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Order number cannot be blank"))
    }

    @Test
    fun testGetOrderTotalWithBlankNumber() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/ /total")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Order number cannot be blank"))
    }

    @Test
    fun testCreateOrderWithValidData() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/order") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "number": "2023-01-01-01",
                    "contents": [
                        {
                            "item": "Pizza",
                            "amount": 2,
                            "price": 10.99
                        },
                        {
                            "item": "Soda",
                            "amount": 3,
                            "price": 1.99
                        }
                    ]
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // Verify the order was created
        val getResponse = client.get("/order/2023-01-01-01")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        assertTrue(getResponse.bodyAsText().contains("Pizza"))
    }


    @Test
    fun testDeleteOrderWithValidNumber() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        // First create an order to delete
        client.post("/order") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "number": "order-to-delete",
                    "contents": [
                        {
                            "item": "Burger",
                            "amount": 1,
                            "price": 8.99
                        }
                    ]
                }
                """.trimIndent()
            )
        }

        // Now delete it
        val response = client.delete("/order/order-to-delete")
        assertEquals(HttpStatusCode.OK, response.status)

        // Verify it's gone
        val getResponse = client.get("/order/order-to-delete")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun testDeleteOrderWithBlankNumber() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.delete("/order/")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Order number cannot be blank"))
    }

    @Test
    fun testGetNonExistentOrder() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/non-existent-order")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Order with ID non-existent-order not found"))
    }

    @Test
    fun testCreateOrderWithInvalidJson() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/order") {
            contentType(ContentType.Application.Json)
            setBody("{ \"invalid\": \"json\" ")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid order data format"))
    }

    @Test
    fun testCreateOrderWithMoreValidationFailures() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/order") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "number": "valid-number",
                    "contents": [
                        {
                            "item": "",
                            "amount": 0,
                            "price": -1.0
                        }
                    ]
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Validation Failed"))
        assertTrue(body.contains("Item name cannot be empty"))
        assertTrue(body.contains("Item amount must be positive"))
        assertTrue(body.contains("Item price cannot be negative"))
    }
    @Test
    fun testDeleteOrderWithSpaceNumber() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.delete("/order/ ")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Order number cannot be blank"))
    }

    @Test
    fun testRouteReThrow() = testApplication {
        val mockService = object : OrderService() {
            override fun createOrder(order: Order): ApiResult<Unit> {
                throw RuntimeException("Unexpected error")
            }
        }
        application {
            configureSerialization()
            routing {
                orderRouting(mockService)
            }
        }
        val response = client.post("/order") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Order("1", emptyList())))
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }
}

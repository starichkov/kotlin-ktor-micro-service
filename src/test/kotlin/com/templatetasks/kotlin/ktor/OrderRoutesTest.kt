package com.templatetasks.kotlin.ktor

import com.templatetasks.kotlin.ktor.plugins.configureRouting
import com.templatetasks.kotlin.ktor.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 08.05.2022 16:30
 */
class OrderRoutesTest {

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
}

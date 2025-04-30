package com.templatetasks.kotlin.ktor

import com.templatetasks.kotlin.ktor.plugins.configureRouting
import com.templatetasks.kotlin.ktor.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 28.04.2025 21:41
 */
class CustomerRoutesTest {

    @Test
    fun testGetCustomerWithValidId() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        val id = UUID.randomUUID().toString()
        val creationResponse = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "id": "$id",
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "test@example.com"
                }
            """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, creationResponse.status)

        val response = client.get("/customer/$id")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetCustomerWithBlankId() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/customer/")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Customer ID cannot be blank"))
    }

    @Test
    fun testCreateCustomerWithValidData() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "id": "3",
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "test@example.com"
                }
            """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testDeleteCustomerWithValidId() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.delete("/customer/1")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testDeleteCustomerWithBlankId() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.delete("/customer/")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Customer ID cannot be blank"))
    }
}

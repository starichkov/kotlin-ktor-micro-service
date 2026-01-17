package com.templatetasks.kotlin.ktor

import com.templatetasks.kotlin.ktor.api.ApiResult
import com.templatetasks.kotlin.ktor.models.Customer
import com.templatetasks.kotlin.ktor.plugins.configureRouting
import com.templatetasks.kotlin.ktor.plugins.configureSerialization
import com.templatetasks.kotlin.ktor.routes.customerRouting
import com.templatetasks.kotlin.ktor.service.CustomerService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.test.*

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

    @Test
    fun testGetNonExistentCustomer() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/customer/non-existent-id")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Customer with ID non-existent-id not found"))
    }

    @Test
    fun testCreateCustomerWithInvalidJson() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody("{ \"invalid\": \"json\" ")
        }
        println("[DEBUG_LOG] Status: ${response.status}")
        println("[DEBUG_LOG] Body: ${response.bodyAsText()}")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid customer data format"))
    }

    @Test
    fun testGetCustomerWithSpaceId() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/customer/ ")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Customer ID cannot be blank"))
    }

    @Test
    fun testCreateDuplicateCustomer() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val id = UUID.randomUUID().toString()
        val customer = Customer(id, "Jane", "Doe", "jane.doe@example.com")
        // First creation
        val response1 = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(customer))
        }
        assertEquals(HttpStatusCode.OK, response1.status)

        // Duplicate creation
        val response2 = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(customer))
        }
        assertEquals(HttpStatusCode.BadRequest, response2.status)
        assertTrue(response2.bodyAsText().contains("Customer with ID $id already exists"))
    }
    @Test
    fun testCreateCustomerWithValidationFailure() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "id": "4",
                    "firstName": "Invalid",
                    "lastName": "User",
                    "email": ""
                }
            """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Validation Failed"))
        assertTrue(response.bodyAsText().contains("Email cannot be empty"))
    }

    @Test
    fun testDeleteCustomerWithSpaceId() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.delete("/customer/ ")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Customer ID cannot be blank"))
    }

    @Test
    fun testServerError() = testApplication {
        val mockService = object : CustomerService() {
            override fun getAllCustomers(): ApiResult<List<Customer>> {
                return ApiResult.Error.ServerError("Simulated error")
            }
        }
        application {
            configureSerialization()
            routing {
                customerRouting(mockService)
            }
        }
        val response = client.get("/customer")
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Internal Server Error"))
    }

    @Test
    fun testRouteReThrow() = testApplication {
        val mockService = object : CustomerService() {
            override fun createCustomer(customer: Customer): ApiResult<Unit> {
                throw RuntimeException("Unexpected error")
            }
        }
        application {
            configureSerialization()
            routing {
                customerRouting(mockService)
            }
        }
        val response = client.post("/customer") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Customer("1", "A", "B", "c@d.com")))
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }
}

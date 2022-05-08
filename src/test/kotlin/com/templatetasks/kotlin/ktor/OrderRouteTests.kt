package com.templatetasks.kotlin.ktor

import com.templatetasks.kotlin.ktor.plugins.configureRouting
import com.templatetasks.kotlin.ktor.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderRouteTests {

    @Test
    fun testGetOrder() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/2022-05-01-01")
        assertEquals(
            """{"number":"2022-05-01-01","contents":[{"item":"Ham Sandwich","amount":2,"price":5.5},{"item":"Water","amount":1,"price":1.5},{"item":"Beer","amount":3,"price":2.3},{"item":"Cheesecake","amount":1,"price":3.75}]}""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetOrderTotal() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val response = client.get("/order/2022-05-01-01/total")
        assertEquals(
            "23.15",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
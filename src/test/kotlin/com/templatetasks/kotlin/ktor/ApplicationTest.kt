package com.templatetasks.kotlin.ktor

import com.templatetasks.kotlin.ktor.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 08.05.2022 15:08
 */
class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
    @Test
    fun testModule() = testApplication {
        application {
            myApplicationModule()
        }
        client.get("/")
    }
}
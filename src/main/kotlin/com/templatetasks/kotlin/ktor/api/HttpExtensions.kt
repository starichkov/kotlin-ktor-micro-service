package com.templatetasks.kotlin.ktor.api

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 28.04.2025 21:20
 */
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: String,
    val message: String,
    val timestamp: String,
    val details: Map<String, List<ValidationError>>? = null
)

suspend inline fun <reified T> ApplicationCall.handleResult(result: ApiResult<T>) {
    when (result) {
        is ApiResult.Success -> respond(result.data as Any)
        is ApiResult.Error -> when (result) {
            is ApiResult.Error.NotFound -> respondError(
                HttpStatusCode.NotFound,
                "Not Found",
                "${result.resource} with ID ${result.id} not found"
            )

            is ApiResult.Error.BadRequest -> respondError(
                HttpStatusCode.BadRequest,
                "Bad Request",
                result.message
            )

            is ApiResult.Error.ValidationFailed -> respondError(
                HttpStatusCode.BadRequest,
                "Validation Failed",
                "One or more fields failed validation",
                mapOf("errors" to result.errors)
            )

            is ApiResult.Error.ServerError -> {
                // Log server errors
                application.log.error("Server error", result.exception)
                respondError(
                    HttpStatusCode.InternalServerError,
                    "Internal Server Error",
                    "An unexpected error occurred"
                )
            }
        }
    }
}

suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    error: String,
    message: String,
    details: Map<String, List<ValidationError>>? = null
) {
    respond(
        status,
        ErrorResponse(
            status = error,
            message = message,
            timestamp = System.currentTimeMillis().toString(),
            details = details
        )
    )
}


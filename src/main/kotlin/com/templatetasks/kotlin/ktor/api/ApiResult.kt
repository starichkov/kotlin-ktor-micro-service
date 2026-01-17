package com.templatetasks.kotlin.ktor.api

import kotlinx.serialization.Serializable

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 28.04.2025 21:12
 */
sealed class ApiResult<out T> {

    data class Success<T>(val data: T) : ApiResult<T>()

    sealed class Error : ApiResult<Nothing>() {

        data class NotFound(val resource: String, val id: String) : Error()

        data class BadRequest(val message: String) : Error()

        data class ValidationFailed(val errors: List<ValidationError>) : Error()

        data class ServerError(val message: String, val exception: Throwable? = null) : Error()
    }
}

@Serializable
data class ValidationError(val field: String, val message: String)

package com.templatetasks.kotlin.ktor.models

import kotlinx.serialization.Serializable

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 2022-05-08 15:57
 */
@Serializable
data class Customer(val id: String, val firstName: String, val lastName: String, val email: String)

val customerStorage = mutableListOf<Customer>()
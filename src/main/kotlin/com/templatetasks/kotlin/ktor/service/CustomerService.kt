package com.templatetasks.kotlin.ktor.service

import com.templatetasks.kotlin.ktor.api.ApiResult
import com.templatetasks.kotlin.ktor.api.ValidationError
import com.templatetasks.kotlin.ktor.models.Customer
import com.templatetasks.kotlin.ktor.models.customerStorage
import kotlin.collections.isNotEmpty

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 28.04.2025 21:13
 */
open class CustomerService() {

    open fun getAllCustomers(): ApiResult<List<Customer>> {
        return ApiResult.Success(customerStorage)
    }

    open fun getCustomer(id: String): ApiResult<Customer> {
        if (id.isBlank()) {
            return ApiResult.Error.BadRequest("Customer ID cannot be blank")
        }

        val customer = customerStorage.find { it.id == id }
        return if (customer != null) {
            ApiResult.Success(customer)
        } else {
            ApiResult.Error.NotFound("Customer", id)
        }
    }

    open fun createCustomer(customer: Customer): ApiResult<Unit> {
        val validationErrors = validateCustomer(customer)
        if (validationErrors.isNotEmpty()) {
            return ApiResult.Error.ValidationFailed(validationErrors)
        }

        if (customerStorage.any { it.id == customer.id }) {
            return ApiResult.Error.BadRequest("Customer with ID ${customer.id} already exists")
        }

        return try {
            customerStorage.add(customer)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error.ServerError("Failed to create customer", e)
        }
    }

    open fun deleteCustomer(id: String): ApiResult<Unit> {
        if (id.isBlank()) {
            return ApiResult.Error.BadRequest("Customer ID cannot be blank")
        }

        return try {
            customerStorage.removeIf { it.id == id }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error.ServerError("Failed to delete customer", e)
        }
    }

    private fun validateCustomer(customer: Customer): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (customer.email.isBlank()) {
            errors.add(ValidationError("email", "Email cannot be empty"))
        }
        // More validations...
        return errors
    }

}

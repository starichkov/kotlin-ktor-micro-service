package com.templatetasks.kotlin.ktor.service

import com.templatetasks.kotlin.ktor.api.ApiResult
import com.templatetasks.kotlin.ktor.api.ValidationError
import com.templatetasks.kotlin.ktor.models.Order
import com.templatetasks.kotlin.ktor.models.orderStorage

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 30.04.2025 23:26
 */
class OrderService() {

    fun getAllOrders(): ApiResult<List<Order>> {
        return ApiResult.Success(orderStorage)
    }

    fun getOrder(number: String): ApiResult<Order> {
        if (number.isBlank()) {
            return ApiResult.Error.BadRequest("Order number cannot be blank")
        }

        val order = orderStorage.find { it.number == number }
        return if (order != null) {
            ApiResult.Success(order)
        } else {
            ApiResult.Error.NotFound("Order", number)
        }
    }

    fun getOrderTotal(number: String): ApiResult<Double> {
        if (number.isBlank()) {
            return ApiResult.Error.BadRequest("Order number cannot be blank")
        }

        val order = orderStorage.find { it.number == number }
        return if (order != null) {
            val total = order.contents.sumOf { it.price * it.amount }
            ApiResult.Success(total)
        } else {
            ApiResult.Error.NotFound("Order", number)
        }
    }

    fun createOrder(order: Order): ApiResult<Unit> {
        val validationErrors = validateOrder(order)
        if (validationErrors.isNotEmpty()) {
            return ApiResult.Error.ValidationFailed(validationErrors)
        }

        return try {
            orderStorage.add(order)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error.ServerError("Failed to create order", e)
        }
    }

    fun deleteOrder(number: String): ApiResult<Unit> {
        if (number.isBlank()) {
            return ApiResult.Error.BadRequest("Order number cannot be blank")
        }

        return try {
            orderStorage.removeIf { it.number == number }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error.ServerError("Failed to delete order", e)
        }
    }

    private fun validateOrder(order: Order): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (order.number.isBlank()) {
            errors.add(ValidationError("number", "Order number cannot be empty"))
        }
        if (order.contents.isEmpty()) {
            errors.add(ValidationError("contents", "Order must contain at least one item"))
        }
        order.contents.forEachIndexed { index, item ->
            if (item.item.isBlank()) {
                errors.add(ValidationError("contents[$index].item", "Item name cannot be empty"))
            }
            if (item.amount <= 0) {
                errors.add(ValidationError("contents[$index].amount", "Item amount must be positive"))
            }
            if (item.price < 0) {
                errors.add(ValidationError("contents[$index].price", "Item price cannot be negative"))
            }
        }
        return errors
    }
}
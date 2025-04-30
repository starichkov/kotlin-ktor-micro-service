package com.templatetasks.kotlin.ktor.service

import com.templatetasks.kotlin.ktor.api.ApiResult
import com.templatetasks.kotlin.ktor.models.Order
import com.templatetasks.kotlin.ktor.models.OrderItem
import com.templatetasks.kotlin.ktor.models.orderStorage
import kotlin.test.*

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 30.04.2025 23:50
 */
class OrderServiceTest {

    private lateinit var orderService: OrderService
    private val originalOrderStorage = orderStorage.toList()

    @BeforeTest
    fun setup() {
        orderService = OrderService()
    }

    @AfterTest
    fun tearDown() {
        orderStorage.clear()
        orderStorage.addAll(originalOrderStorage)
    }

    @Test
    fun testGetAllOrders() {
        val result = orderService.getAllOrders()

        assertTrue(result is ApiResult.Success)
        val orders = result.data
        assertEquals(originalOrderStorage.size, orders.size)
        assertEquals(originalOrderStorage, orders)
    }

    @Test
    fun testGetOrderWithValidNumber() {
        val orderNumber = "2022-05-01-01"

        val result = orderService.getOrder(orderNumber)

        assertTrue(result is ApiResult.Success)
        val order = result.data
        assertEquals(orderNumber, order.number)
        assertEquals(4, order.contents.size)
        assertTrue(order.contents.any { it.item == "Ham Sandwich" })
    }

    @Test
    fun testGetOrderWithBlankNumber() {
        val result = orderService.getOrder(" ")

        assertTrue(result is ApiResult.Error.BadRequest)
        assertEquals("Order number cannot be blank", result.message)
    }

    @Test
    fun testGetOrderWithNonExistentNumber() {
        val result = orderService.getOrder("non-existent")

        assertTrue(result is ApiResult.Error.NotFound)
        val error = result
        assertEquals("Order", error.resource)
        assertEquals("non-existent", error.id)
    }

    @Test
    fun testGetOrderTotalWithValidNumber() {
        val orderNumber = "2022-05-01-01"

        val result = orderService.getOrderTotal(orderNumber)

        assertTrue(result is ApiResult.Success)
        val total = result.data
        assertEquals(23.15, total, 0.001)
    }

    @Test
    fun testGetOrderTotalWithBlankNumber() {
        val result = orderService.getOrderTotal("")

        assertTrue(result is ApiResult.Error.BadRequest)
        assertEquals("Order number cannot be blank", result.message)
    }

    @Test
    fun testGetOrderTotalWithNonExistentNumber() {
        val result = orderService.getOrderTotal("non-existent")

        assertTrue(result is ApiResult.Error.NotFound)
        val error = result
        assertEquals("Order", error.resource)
        assertEquals("non-existent", error.id)
    }

    @Test
    fun testCreateOrderWithValidData() {
        val newOrder = Order(
            "test-order",
            listOf(
                OrderItem("Test Item", 2, 10.0)
            )
        )
        val initialSize = orderStorage.size

        val result = orderService.createOrder(newOrder)

        assertTrue(result is ApiResult.Success)
        assertEquals(initialSize + 1, orderStorage.size)
        assertNotNull(orderStorage.find { it.number == "test-order" })
    }

    @Test
    fun testCreateOrderWithInvalidData() {
        val invalidOrder = Order(
            "",
            listOf(
                OrderItem("Test Item", 2, 10.0)
            )
        )

        val result = orderService.createOrder(invalidOrder)

        assertTrue(result is ApiResult.Error.ValidationFailed)
        val errors = result.errors
        assertTrue(errors.any { it.field == "number" })
    }

    @Test
    fun testCreateOrderWithEmptyContents() {
        val invalidOrder = Order("test-order", emptyList())

        val result = orderService.createOrder(invalidOrder)

        assertTrue(result is ApiResult.Error.ValidationFailed)
        val errors = result.errors
        assertTrue(errors.any { it.field == "contents" })
    }

    @Test
    fun testCreateOrderWithInvalidItem() {
        val invalidOrder = Order(
            "test-order",
            listOf(
                OrderItem("", 2, 10.0)
            )
        )

        val result = orderService.createOrder(invalidOrder)

        assertTrue(result is ApiResult.Error.ValidationFailed)
        val errors = result.errors
        assertTrue(errors.any { it.field.contains("item") })
    }

    @Test
    fun testCreateOrderWithInvalidAmount() {
        val invalidOrder = Order(
            "test-order",
            listOf(
                OrderItem("Test Item", 0, 10.0)
            )
        )

        val result = orderService.createOrder(invalidOrder)

        assertTrue(result is ApiResult.Error.ValidationFailed)
        val errors = result.errors
        assertTrue(errors.any { it.field.contains("amount") })
    }

    @Test
    fun testCreateOrderWithNegativePrice() {
        val invalidOrder = Order(
            "test-order",
            listOf(
                OrderItem("Test Item", 2, -10.0)
            )
        )

        val result = orderService.createOrder(invalidOrder)

        assertTrue(result is ApiResult.Error.ValidationFailed)
        val errors = result.errors
        assertTrue(errors.any { it.field.contains("price") })
    }

    @Test
    fun testDeleteOrderWithValidNumber() {
        val orderNumber = "2022-05-01-01"
        val initialSize = orderStorage.size

        val result = orderService.deleteOrder(orderNumber)

        assertTrue(result is ApiResult.Success)
        assertEquals(initialSize - 1, orderStorage.size)
        assertNull(orderStorage.find { it.number == orderNumber })
    }

    @Test
    fun testDeleteOrderWithBlankNumber() {
        val result = orderService.deleteOrder(" ")

        assertTrue(result is ApiResult.Error.BadRequest)
        assertEquals("Order number cannot be blank", result.message)
    }
}

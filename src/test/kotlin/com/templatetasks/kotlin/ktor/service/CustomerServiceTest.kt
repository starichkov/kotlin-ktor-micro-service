package com.templatetasks.kotlin.ktor.service

import com.templatetasks.kotlin.ktor.api.ApiResult
import com.templatetasks.kotlin.ktor.models.Customer
import com.templatetasks.kotlin.ktor.models.customerStorage
import kotlin.test.*

/**
 * @author Vadim Starichkov (starichkovva@gmail.com)
 * @since 30.04.2025 23:51
 */
class CustomerServiceTest {

    private lateinit var customerService: CustomerService
    private val originalCustomerStorage = customerStorage.toList()

    @BeforeTest
    fun setup() {
        customerService = CustomerService()

        if (customerStorage.isEmpty()) {
            customerStorage.add(Customer("test-id-1", "John", "Doe", "john.doe@example.com"))
        }
    }

    @AfterTest
    fun tearDown() {
        customerStorage.clear()
        customerStorage.addAll(originalCustomerStorage)
    }

    @Test
    fun testGetAllCustomers() {
        val result = customerService.getAllCustomers()

        assertTrue(result is ApiResult.Success)
        val customers = result.data
        assertEquals(customerStorage.size, customers.size)
        assertEquals(customerStorage, customers)
    }

    @Test
    fun testGetCustomerWithValidId() {
        val testCustomer = Customer("test-id-2", "Jane", "Smith", "jane.smith@example.com")
        customerStorage.add(testCustomer)

        val result = customerService.getCustomer(testCustomer.id)

        assertTrue(result is ApiResult.Success)
        val customer = result.data
        assertEquals(testCustomer.id, customer.id)
        assertEquals(testCustomer.firstName, customer.firstName)
        assertEquals(testCustomer.lastName, customer.lastName)
        assertEquals(testCustomer.email, customer.email)
    }

    @Test
    fun testGetCustomerWithBlankId() {
        val result = customerService.getCustomer(" ")

        assertTrue(result is ApiResult.Error.BadRequest)
        assertEquals("Customer ID cannot be blank", result.message)
    }

    @Test
    fun testGetCustomerWithNonExistentId() {
        val result = customerService.getCustomer("non-existent")

        assertTrue(result is ApiResult.Error.NotFound)
        val error = result
        assertEquals("Customer", error.resource)
        assertEquals("non-existent", error.id)
    }

    @Test
    fun testCreateCustomerWithValidData() {
        val newCustomer = Customer(
            "test-id-3",
            "Bob",
            "Johnson",
            "bob.johnson@example.com"
        )
        val initialSize = customerStorage.size

        val result = customerService.createCustomer(newCustomer)

        assertTrue(result is ApiResult.Success)
        assertEquals(initialSize + 1, customerStorage.size)
        assertNotNull(customerStorage.find { it.id == newCustomer.id })
    }

    @Test
    fun testCreateCustomerWithBlankEmail() {
        val invalidCustomer = Customer(
            "test-id-4",
            "Invalid",
            "Customer",
            ""
        )

        val result = customerService.createCustomer(invalidCustomer)

        assertTrue(result is ApiResult.Error.ValidationFailed)
        val errors = result.errors
        assertTrue(errors.any { it.field == "email" })
    }

    @Test
    fun testDeleteCustomerWithValidId() {
        val testCustomer = Customer("test-id-to-delete", "Delete", "Me", "delete.me@example.com")
        customerStorage.add(testCustomer)
        val initialSize = customerStorage.size

        val result = customerService.deleteCustomer(testCustomer.id)

        assertTrue(result is ApiResult.Success)
        assertEquals(initialSize - 1, customerStorage.size)
        assertNull(customerStorage.find { it.id == testCustomer.id })
    }

    @Test
    fun testDeleteCustomerWithBlankId() {
        val result = customerService.deleteCustomer(" ")

        assertTrue(result is ApiResult.Error.BadRequest)
        assertEquals("Customer ID cannot be blank", result.message)
    }
}

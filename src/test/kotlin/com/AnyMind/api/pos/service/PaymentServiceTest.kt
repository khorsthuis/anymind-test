package com.AnyMind.api.pos.service

import com.AnyMind.api.enums.PaymentMethod
import com.AnyMind.api.pos.dto.NewSaleDto
import com.AnyMind.api.pos.dto.Payment
import com.AnyMind.api.pos.repository.PaymentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

internal class PaymentServiceTest {

    lateinit var paymentRepository: PaymentRepository
    lateinit var paymentService: PaymentService

    /**
     * Before method that creates the mock-classes
     */
    @BeforeEach
    fun setUp() {
        paymentRepository = mock(PaymentRepository::class.java)
        paymentService = PaymentService(paymentRepository)
    }

    /**
     * Funciton to test the service-level method that should return all payment-methods from the db
     */
    @Test
    fun getAll() {
        whenever(paymentRepository.getAll()).thenReturn(
            listOf(
                Payment(
                    id = 1,
                    method = "cash",
                    priceModifierLower = 0.9,
                    priceModifierUpper = 1.0,
                    pointsApplicable = 0.05
                ),
                Payment(
                    id = 2,
                    method = "cash_on_delivery",
                    priceModifierLower = 1.0,
                    priceModifierUpper = 1.02,
                    pointsApplicable = 0.05
                )
            )
        )
        val out = paymentService.getAll()
        assertEquals(2, out.size)
        assertEquals("cash", out[0].method)
        assertEquals(0.9, out[0].priceModifierLower)
        assertEquals(1.0, out[0].priceModifierUpper)
        assertEquals(0.05, out[0].pointsApplicable)

        assertEquals(2, out.size)
        assertEquals("cash_on_delivery", out[1].method)
        assertEquals(1.0, out[1].priceModifierLower)
        assertEquals(1.02, out[1].priceModifierUpper)
        assertEquals(0.05, out[1].pointsApplicable)
    }

    /**
     * Testing method that verifies the correct calclation of a valid NewSaleDto
     */
    @Test
    fun calculateValidSale() {
        whenever(paymentRepository.getBy(PaymentMethod.cash)).thenReturn(
            Payment(
                id = 1,
                method = "cash",
                priceModifierLower = 0.9,
                priceModifierUpper = 1.0,
                pointsApplicable = 0.05
            )
        )
        val time = OffsetDateTime.now()
        val newSale = NewSaleDto(price = 100.0, priceModifier = 0.9, paymentMethod = "cash", datetime = time)

        val result = paymentService.calculateSale(newSale)
        assertEquals(time, result.datetime)
        assertEquals(90.0, result.price)
        assertEquals(4, result.points)
    }

    /**
     * Testing method that should throw an error provided the price-modifier is out of range
     * @exception IllegalArgumentException is expected to be thrown
     */
    @Test
    fun calculateInvalidSaleModifier() {
        whenever(paymentRepository.getBy(PaymentMethod.cash)).thenReturn(
            Payment(
                id = 1,
                method = "cash",
                priceModifierLower = 0.9,
                priceModifierUpper = 1.0,
                pointsApplicable = 0.05
            )
        )
        val time = OffsetDateTime.now()
        val newSale = NewSaleDto(price = 100.0, priceModifier = 0.5, paymentMethod = "cash", datetime = time)

        assertThrows<IllegalArgumentException> { paymentService.calculateSale(newSale) }
    }

    /**
     * Testing method that should throw an error provided the payment-method is not supported
     * @exception IllegalArgumentException is expected to be thrown
     */
    @Test
    fun calculateInvalidSaleMethod() {
        whenever(paymentRepository.getBy(PaymentMethod.cash)).thenReturn(
            Payment(
                id = 1,
                method = "cash",
                priceModifierLower = 0.9,
                priceModifierUpper = 1.0,
                pointsApplicable = 0.05
            )
        )
        val time = OffsetDateTime.now()
        val newSale = NewSaleDto(price = 100.0, priceModifier = 0.9, paymentMethod = "not_supported", datetime = time)

        assertThrows<IllegalArgumentException> { paymentService.calculateSale(newSale) }
    }
}

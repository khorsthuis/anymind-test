package com.AnyMind.api.pos.service

import com.AnyMind.api.enums.PaymentMethod
import com.AnyMind.api.pos.dto.NewSaleDto
import com.AnyMind.api.pos.dto.Payment
import com.AnyMind.api.pos.dto.Sale
import com.AnyMind.api.pos.repository.PaymentRepository
import com.AnyMind.api.pos.repository.SaleRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.isA
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

/**
 * Test class that tests the functionality of the SaleService class
 */
internal class SaleServiceTest {

    lateinit var saleRepository: SaleRepository
    lateinit var paymentRepository: PaymentRepository
    lateinit var paymentService: PaymentService
    lateinit var saleService: SaleService

    /**
     * Setting up the mock-classes
     */
    @BeforeEach
    fun setUp() {
        saleRepository = mock(SaleRepository::class.java)
        paymentRepository = mock(PaymentRepository::class.java)
        paymentService = PaymentService(paymentRepository)
        saleService = SaleService(saleRepository, paymentService)
    }

    /**
     * Simple test to verify that the service-level getAll() method returns all the sales in the database.
     */
    @Test
    fun getAll() {
        whenever(saleRepository.getAll()).thenReturn(
            listOf(
                Sale(id = 0, finalPrice = 10.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:26.851+09:00"), points = 1),
                Sale(id = 1, finalPrice = 11.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:27.851+09:00"), points = 2),
                Sale(id = 2, finalPrice = 12.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:28.851+09:00"), points = 3),
                Sale(id = 3, finalPrice = 13.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:29.851+09:00"), points = 4),
                Sale(id = 4, finalPrice = 14.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:30.851+09:00"), points = 5)
            )
        )
        val result = saleService.getAll()
        assertEquals(5, result.size)

        assertEquals(0, result[0].id)
        assertEquals(10.0, result[0].finalPrice)
        assertEquals("2023-03-09T15:46:26.851+09:00", result[0].datetime.toString())
    }

    /**
     * Testing the sale-service level correctly calculates the new-sale and adds it to the db
     * returning the correctly mapped object through the repository- and service-levels.
     */
    @Test
    fun addSale() {
        val time = OffsetDateTime.now()
        // db mock for the payment-calculation db call
        whenever(paymentRepository.getBy(PaymentMethod.cash)).thenReturn(
            Payment(
                id = 1,
                method = "cash",
                priceModifierLower = 0.9,
                priceModifierUpper = 1.0,
                pointsApplicable = 0.05
            )
        )

        // db mock for the add-sale db call:
        whenever(saleRepository.create(isA())).thenReturn(
            Sale(id = 0, finalPrice = 90.0, datetime = time, points = 4)
        )
        // caling service under test and evaluating the outcome
        val newSale = NewSaleDto(price = 100.0, priceModifier = 0.9, paymentMethod = "cash", datetime = time)
        val result = saleService.addSale(newSale)

        assertEquals(0, result.id)
        assertEquals(time, result.datetime)
        assertEquals(4, result.points)
        assertEquals(90.0, result.finalPrice)
    }

    /**
     * Test to verify the result of the repository-implementation is returned when calling the service-level method.
     */
    @Test
    fun getAllBetweenDates() {
        val begin = OffsetDateTime.parse("2023-03-09T15:46:26.851+09:00")
        val end = OffsetDateTime.parse("2023-03-09T15:46:29.851+09:00")

        whenever(saleRepository.getAllBetweenDates(begin, end)).thenReturn(
            listOf(
                Sale(id = 0, finalPrice = 10.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:26.851+09:00"), points = 1),
                Sale(id = 1, finalPrice = 11.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:27.851+09:00"), points = 2),
                Sale(id = 2, finalPrice = 12.0, datetime = OffsetDateTime.parse("2023-03-09T15:46:28.851+09:00"), points = 3)
            )
        )
        val result = saleService.getAllBetweenDates(begin, end)

        assertEquals(3, result.size)
    }
}

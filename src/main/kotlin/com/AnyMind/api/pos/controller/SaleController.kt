package com.AnyMind.api.pos.controller

import com.AnyMind.api.pos.dto.*
import com.AnyMind.api.pos.service.PaymentService
import com.AnyMind.api.pos.service.SaleService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.time.OffsetDateTime

@Controller
class SaleController(
    private val saleService: SaleService,
    private val paymentService: PaymentService
) {

    /**
     * Controller method that returns all available payment-methods
     */
    @QueryMapping
    fun paymentMethods(): Iterable<Payment> {
        return paymentService.getAll()
    }

    /**
     * Controller method that returns all historical sales
     */
    @QueryMapping
    fun sales(): Iterable<Sale> {
        return saleService.getAll()
    }

    /**
     * Controller method that returns all sales between 2 dates
     */
    @QueryMapping
    fun getSales(@Argument startDateTime: OffsetDateTime, @Argument endDateTime: OffsetDateTime): Iterable<Sale> {
        return saleService.getAllBetweenDates(startDateTime, endDateTime)
    }

    /**
     * Controller method that stores a new sale in the db and returns the calculated values for the sale
     */
    @MutationMapping
    fun addSale(@Argument sale: NewSaleDto): Any {
        return try {
            saleService.addSale(sale)
        } catch (exception: Exception) {
            NewSaleReturnFailedPayload(exception.message ?: "Unexpected error ocured")
        }
    }
}

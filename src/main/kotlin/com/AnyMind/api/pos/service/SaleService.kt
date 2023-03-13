package com.AnyMind.api.pos.service

import com.AnyMind.api.pos.dto.NewSaleDto
import com.AnyMind.api.pos.dto.Sale
import com.AnyMind.api.pos.repository.SaleRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

/**
 * Service-class that handles the retrieval and storage of new and historical sales.
 */
@Service
class SaleService(
    private val saleRepository: SaleRepository,
    private val paymentService: PaymentService
) {

    /**
     * Method that returns all available sales record
     * @return list of sales sorted according to sales-time
     */
    fun getAll(): List<Sale> {
        return saleRepository.getAll().sortedBy { it.datetime }
    }

    /**
     * Method that calls validation/calculation of sale and then stores it in the db
     * @return Sale object as stored in the db
     */
    fun addSale(newSale: NewSaleDto): Sale {
        val saleCreateDto = paymentService.calculateSale(newSale)
        return saleRepository.create(saleCreateDto)
    }

    /**
     * Method that returns all available sales between start- and end-date
     * @param begin: start-date
     * @param end: end-date
     * @return list of sales that match aforementioned criteria sorted according to sales-time
     */
    fun getAllBetweenDates(begin: OffsetDateTime, end: OffsetDateTime): List<Sale> {
        return saleRepository.getAllBetweenDates(begin, end).sortedBy { it.datetime }
    }
}

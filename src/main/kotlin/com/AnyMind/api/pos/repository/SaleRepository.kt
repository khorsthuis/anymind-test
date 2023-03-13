package com.AnyMind.api.pos.repository

import com.AnyMind.api.pos.dto.Sale
import com.AnyMind.api.pos.dto.SaleCreateDto
import java.time.OffsetDateTime

/**
 * Interface class to define the requirement of the Sale-repository class.
 * When database-implementations are switched this interface class will define the contract between service and
 * controller class
 */
interface SaleRepository {

    fun getAll(): List<Sale>

    fun create(newSale: SaleCreateDto): Sale

    fun getAllBetweenDates(begin: OffsetDateTime, end: OffsetDateTime): List<Sale>
}

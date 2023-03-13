package com.AnyMind.api.pos.dto

import java.time.OffsetDateTime

/**
 * Class that represents a calculated and stored sale.
 */
data class Sale(
    val id: Long,
    val finalPrice: Double,
    val datetime: OffsetDateTime,
    val points: Int
)

/**
 * Class that contains the user-provided details for a new sale
 */
data class NewSaleDto(
    val price: Double,
    val priceModifier: Double,
    val paymentMethod: String,
    val datetime: OffsetDateTime?
)

/**
 * Class that contains the calculated and validated values for a sale.
 * This class is used to insert record into the db
 */
data class SaleCreateDto(
    val price: Double,
    val datetime: OffsetDateTime,
    val points: Int
)

/**
 * Payload class that is returned when a new-sale mutation fails
 */
data class NewSaleReturnFailedPayload(
    val errorMessage: String
)

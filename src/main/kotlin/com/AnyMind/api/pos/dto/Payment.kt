package com.AnyMind.api.pos.dto

/**
 * Class that outlines the various variables that make up a Payment-method
 */
data class Payment(
    val id: Long,
    val method: String,
    val priceModifierLower: Double,
    val priceModifierUpper: Double,
    val pointsApplicable: Double
)

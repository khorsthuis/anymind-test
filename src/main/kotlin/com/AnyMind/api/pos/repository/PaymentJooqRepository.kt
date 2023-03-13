package com.AnyMind.api.pos.repository

import com.AnyMind.api.Tables.PAYMENT_METHODS
import com.AnyMind.api.enums.PaymentMethod
import com.AnyMind.api.pos.dto.Payment
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class PaymentJooqRepository(
    private val dslContext: DSLContext
) : PaymentRepository {

    /**
     * Jooq function that returns all payment-options from the db
     */
    override fun getAll(): List<Payment> {
        return dslContext.select(
            getModelColumns()
        )
            .from(PAYMENT_METHODS)
            .fetchInto(Payment::class.java)
    }

    /**
     * Jooq function that fetches the payment-related details
     * @param paymentMethod: ENUM paymentMethod for which to fetch records
     */
    override fun getBy(paymentMethod: PaymentMethod): Payment? {
        return dslContext.select(
            getModelColumns()
        ).from(PAYMENT_METHODS)
            .where(PAYMENT_METHODS.PAYMENT_METHOD.eq(paymentMethod))
            .fetchInto(Payment::class.java)
            .firstOrNull()
    }

    /**
     * Private function that helps to reduce code-duplication when fetching the records from the db.
     */
    private fun getModelColumns() = listOf(
        PAYMENT_METHODS.ID.`as`(Payment::id.name),
        PAYMENT_METHODS.PAYMENT_METHOD.`as`(Payment::method.name),
        PAYMENT_METHODS.PRICE_MODIFIER_LOWER.`as`(Payment::priceModifierLower.name),
        PAYMENT_METHODS.PRICE_MODIFIER_UPPER.`as`(Payment::priceModifierUpper.name),
        PAYMENT_METHODS.POINTS_APPLICABLE.`as`(Payment::pointsApplicable.name)
    )
}

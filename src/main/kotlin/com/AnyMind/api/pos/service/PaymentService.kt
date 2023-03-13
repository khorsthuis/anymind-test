package com.AnyMind.api.pos.service

import com.AnyMind.api.enums.PaymentMethod
import com.AnyMind.api.pos.dto.NewSaleDto
import com.AnyMind.api.pos.dto.Payment
import com.AnyMind.api.pos.dto.SaleCreateDto
import com.AnyMind.api.pos.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.time.OffsetDateTime

/**
 * Service class that handles the validation and calculation of a sale by comparing the user-provided
 * details with the payment-information available.
 */
@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {

    /**
     * Method that returns all the payment-methods available
     */
    fun getAll(): List<Payment> {
        return paymentRepository.getAll()
    }

    /**
     * Method that calculates and checks validity of a new sale
     * @param newSale: Sale that is to be verified
     * @return a valid SaleCreateDto that may be inserted into the database
     */
    fun calculateSale(newSale: NewSaleDto): SaleCreateDto {
        val paymentMethodDto = paymentRepository.getBy(resolvePaymentMethod(newSale.paymentMethod))

        validatePayment(newSale, paymentMethodDto)

        val finalSalePrice = newSale.price * newSale.priceModifier
        return SaleCreateDto(
            price = finalSalePrice,
            datetime = newSale.datetime ?: OffsetDateTime.now(),
            points = (finalSalePrice * paymentMethodDto!!.pointsApplicable).toBigDecimal()
                .setScale(0, RoundingMode.FLOOR).toInt()
        )
    }

    /**
     * Method to resolve the enum class for the provided payment-method string
     * @exception IllegalArgumentException when payment-method cannot be converted and therefore is not supported
     */
    private fun resolvePaymentMethod(paymentMethod: String): PaymentMethod {
        return try {
            PaymentMethod.valueOf(paymentMethod.lowercase())
        } catch (e: Exception) {
            throw IllegalArgumentException("Specified payment method is not supported")
        }
    }

    /**
     * Method to ensure that he provided price modifier for the new payment is within the upper and lower
     * limits for that payment-type
     * @param newSale: The sale that is to be verified
     * @param payment: The Payment object with the boundries for this sale
     * @exception IllegalArgumentException when supplied price-modifier is outside of acceptable range
     * @exception NullPointerException when the payment-method could not be found in the database
     */
    private fun validatePayment(newSale: NewSaleDto, payment: Payment?) {
        if (payment != null) {
            if (newSale.priceModifier > payment.priceModifierUpper || newSale.priceModifier < payment.priceModifierLower) {
                throw IllegalArgumentException("price-modifier is outside of the defined range")
            }
        } else {
            throw NullPointerException("payment method cannot be found")
        }
    }
}

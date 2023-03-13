package com.AnyMind.api.pos.repository

import com.AnyMind.api.enums.PaymentMethod
import com.AnyMind.api.pos.dto.Payment

/**
 * Interface class to define the requirement of the Payment-repository class.
 * When database-implementations are switched this interface class will define the contract between service and
 * controller class
 */
interface PaymentRepository {

    fun getAll(): List<Payment>

    fun getBy(paymentMethod: PaymentMethod): Payment?
}

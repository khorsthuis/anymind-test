package com.AnyMind.api.pos.controller

import com.AnyMind.api.pos.dto.Payment
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.graphql.test.tester.GraphQlTester

/**
 * This is an example of how I would approach testing the controller class using the GraphQlTester.
 * Because I did not want to spend the time to create a testing-db or mocking the db I have decided to abandon
 * implementing tests here.
 *
 * In a real-world scenario I would of course test here as well. Preferably with a docker-image of a database that is
 * spun up when instantiating the tests.
 */
@GraphQlTest(SaleController::class)
internal class SaleControllerIntTest {

    @Autowired
    lateinit var graphQlTester: GraphQlTester

//    @Test
    fun testFindAllPaymentMethodsShouldReturnCorrectly() {
        val document = """
            query{
              paymentMethods{ 
                method,
                priceModifierLower,
                priceModifierUpper,
                pointsApplicable
              }
            }
        """
        graphQlTester.document(document)
            .execute()
            .path("paymentMethods")
            .entityList(Payment::class.java)
            .hasSize(6)
    }
}

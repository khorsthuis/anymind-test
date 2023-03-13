package com.AnyMind.api.pos.repository

import com.AnyMind.api.Tables.SALES
import com.AnyMind.api.pos.dto.Sale
import com.AnyMind.api.pos.dto.SaleCreateDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class SaleJooqRepository(
    private val dslContext: DSLContext
) : SaleRepository {

    /**
     * Jooq funciton that retrieves all sales from the db
     * @return list of Sales
     */
    override fun getAll(): List<Sale> {
        return dslContext.select(
            SALES.ID.`as`(Sale::id.name),
            SALES.AMOUNT.`as`(Sale::finalPrice.name),
            SALES.SALE_DATETIME.`as`(Sale::datetime.name),
            SALES.POINTS_PROVIDED.`as`(Sale::points.name)
        ).from(SALES)
            .fetchInto(Sale::class.java)
    }

    /**
     * Jooq function that inserts a new sale into the database
     * @return newly created Sale-object as stored in the db
     */
    override fun create(newSale: SaleCreateDto): Sale {
        dslContext.insertInto(SALES, SALES.AMOUNT, SALES.SALE_DATETIME, SALES.POINTS_PROVIDED)
            .values(newSale.price, newSale.datetime, newSale.points)
            .execute()

        return getBy(newSale.datetime)!!
    }

    /**
     * Jooq function that gets all the sales-reccords between two dates
     * @return list of sales that meet criteria
     */
    override fun getAllBetweenDates(begin: OffsetDateTime, end: OffsetDateTime): List<Sale> {
        return dslContext.select(
            getModelColumns()
        ).from(SALES)
            .where(SALES.SALE_DATETIME.between(begin, end))
            .fetchInto(Sale::class.java)
    }

    /**
     * private function used when insert is executed to retrieve the newly-created
     * and stored object from db
     */
    private fun getBy(salesDateTime: OffsetDateTime): Sale? {
        return dslContext.select(
            getModelColumns()
        ).from(SALES)
            .where(SALES.SALE_DATETIME.eq(salesDateTime))
            .fetchInto(Sale::class.java)
            .firstOrNull()
    }

    /**
     * Private function that helps to reduce code-duplication when fetching the records from the db.
     */
    private fun getModelColumns() = listOf(
        SALES.ID.`as`(Sale::id.name),
        SALES.AMOUNT.`as`(Sale::finalPrice.name),
        SALES.SALE_DATETIME.`as`(Sale::datetime.name),
        SALES.POINTS_PROVIDED.`as`(Sale::points.name)
    )
}

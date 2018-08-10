package ru.popov.bodya.data.database.transactions.converters.entities

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import ru.popov.bodya.domain.transactions.models.IncomeCategory
import ru.popov.bodya.domain.transactions.models.TransactionsCategory

/**
 *  @author popovbodya
 */
class TransactionTypeConverterTest {

    private lateinit var transactionTypeConverter: TransactionTypeConverter

    @Before
    fun setUp() {
        transactionTypeConverter = TransactionTypeConverter()
    }

    @Test
    fun testFromTransactionTypeToString() {
        val expected = "GIFT"
        val actual = transactionTypeConverter.fromTransactionTypeToString(TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.GIFT))
        assertThat(actual, `is`(expected))
    }

    @Test
    fun testFromIntToTransactionType() {
        val expected = "FAMILY"
        val transactionsCategory = transactionTypeConverter.fromStringToTransactionType(expected)
        val expense = transactionsCategory as? TransactionsCategory.ExpenseTransactionsCategory
        assertThat(expense, `is`(notNullValue()))
        assertThat(expense?.expenseCategory?.name, `is`(expected))
    }

}
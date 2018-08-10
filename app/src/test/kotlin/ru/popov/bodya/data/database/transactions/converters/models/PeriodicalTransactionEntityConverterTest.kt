package ru.popov.bodya.data.database.transactions.converters.models

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import ru.popov.bodya.data.database.transactions.entities.PeriodicalTransactionEntity
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.IncomeCategory
import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 *  @author popovbodya
 */
class PeriodicalTransactionEntityConverterTest {

    private lateinit var periodicalTransactionEntityConverter: PeriodicalTransactionEntityConverter

    @Before
    fun setUp() {
        periodicalTransactionEntityConverter = PeriodicalTransactionEntityConverter()
    }

    @Test
    fun testReverse() {
        val expected = createPeriodicalTransaction()
        val actual = periodicalTransactionEntityConverter.reverse(createPeriodicalTransactionEntity())
        assertThat(actual.id, `is`(expected.id))
        assertThat(actual.period, `is`(expected.period))
        assertThat(actual.periodDescription, `is`(expected.periodDescription))
        assertThat(actual.amount, `is`(expected.amount))
        assertThat(actual.currency, `is`(expected.currency))
    }

    @Test
    fun testConvert() {
        val expected = createPeriodicalTransactionEntity()
        val actual = periodicalTransactionEntityConverter.convert(createPeriodicalTransaction())
        assertThat(actual.id, `is`(expected.id))
        assertThat(actual.period, `is`(expected.period))
        assertThat(actual.periodDescription, `is`(expected.periodDescription))
        assertThat(actual.amount, `is`(expected.amount))
        assertThat(actual.currency, `is`(expected.currency))
    }

    private fun createPeriodicalTransactionEntity() =
            PeriodicalTransactionEntity(1, WalletType.CASH, Currency.EUR, TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.SALARY),
                    50.0, 123456789L, "imba", 10000, "Infinity")

    private fun createPeriodicalTransaction() =
            PeriodicalTransaction(1, WalletType.CASH, Currency.EUR, TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.SALARY),
                    50.0, 123456789L, "imba", 10000, "Infinity")

}
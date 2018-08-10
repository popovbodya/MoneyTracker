package ru.popov.bodya.domain.transactions

import io.reactivex.Completable
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import ru.popov.bodya.data.repositories.PeriodicalTransactionsRepository
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.*
import ru.popov.bodya.domain.transactions.models.TransactionsCategory.ExpenseTransactionsCategory
import ru.popov.bodya.domain.transactions.models.TransactionsCategory.IncomeTransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType.CASH
import ru.popov.bodya.domain.transactions.models.WalletType.CREDIT_CARD

/**
 * @author popovbodya
 */
class PeriodicalTransactionsInteractorTest {

    companion object {
        const val TIME_CREATED = 0L
        const val PERIOD = 50L
        const val PERIOD_DESC = "Infinity"

    }

    private lateinit var periodicalTransactionsInteractor: PeriodicalTransactionsInteractor
    private lateinit var periodicalTransactionsRepository: PeriodicalTransactionsRepository

    @Before
    fun setUp() {
        periodicalTransactionsRepository = mock(PeriodicalTransactionsRepository::class.java)
        periodicalTransactionsInteractor = PeriodicalTransactionsInteractor(periodicalTransactionsRepository)
    }

    @Test
    fun testGetAllPeriodicalTransactionsByWallet() {
        val expected = createStubPeriodicalTransactionList()
        `when`(periodicalTransactionsRepository.getAllPeriodicalTransactionsByWallet(WalletType.CASH)).thenReturn(Single.just(expected))
        periodicalTransactionsInteractor.getAllPeriodicalTransactionsByWallet(WalletType.CASH)
                .test()
                .assertValue(expected)
                .assertComplete()
        verify(periodicalTransactionsRepository).getAllPeriodicalTransactionsByWallet(CASH)
        verifyNoMoreInteractions(periodicalTransactionsRepository)
    }

    @Test
    fun testAddPeriodicalTransaction() {
        `when`(periodicalTransactionsRepository.addPeriodicalTransaction(anyMock(PeriodicalTransaction::class.java))).thenReturn(Completable.complete())
        periodicalTransactionsInteractor.createTransaction(CREDIT_CARD, ExpenseTransactionsCategory(ExpenseCategory.FOOD), Currency.EUR, 30.0, 123456789, "imba", PERIOD, PERIOD_DESC)
                .test()
                .assertComplete()
    }

    @Test
    fun testCreateTransactionListBasedOnPeriodicalTransactions() {
        val methodCallTime = 300L
        val transactionList = createStubPeriodicalTransactionList()
        val expectedCount = transactionList.size * (methodCallTime / PERIOD)
        val actual: List<Transaction> = periodicalTransactionsInteractor.createTransactionListBasedOnPeriodicalTransactions(transactionList, methodCallTime)
        assertThat(actual.size, `is`(expectedCount.toInt()))
        verify(periodicalTransactionsRepository, times(transactionList.size)).modifyPeriodicalTransactionTimeUpdated(ArgumentMatchers.anyLong(), anyMock(WalletType::class.java), ArgumentMatchers.anyLong())
    }

    private fun <T> anyMock(type: Class<T>): T = Mockito.any<T>(type)


    private fun createStubPeriodicalTransactionList(): List<PeriodicalTransaction> {
        val list: MutableList<PeriodicalTransaction> = mutableListOf()
        list.addAll(createStubExpensePeriodicalTransactionList())
        list.addAll(createStubIncomePeriodicalTransactionList())
        return list
    }

    private fun createStubExpensePeriodicalTransactionList(): List<PeriodicalTransaction> {
        return listOf(
                PeriodicalTransaction(0, CREDIT_CARD, Currency.USD, ExpenseTransactionsCategory(ExpenseCategory.FAMILY), 50.0, TIME_CREATED, "1", PERIOD, PERIOD_DESC),
                PeriodicalTransaction(0, CREDIT_CARD, Currency.USD, ExpenseTransactionsCategory(ExpenseCategory.FAMILY), 25.0, TIME_CREATED, "2", PERIOD, PERIOD_DESC)
        )
    }

    private fun createStubIncomePeriodicalTransactionList(): List<PeriodicalTransaction> {
        return listOf(
                PeriodicalTransaction(0, CREDIT_CARD, Currency.EUR, IncomeTransactionsCategory(IncomeCategory.GIFT), 30.0, TIME_CREATED, "3", PERIOD, PERIOD_DESC),
                PeriodicalTransaction(0, CREDIT_CARD, Currency.USD, IncomeTransactionsCategory(IncomeCategory.SALARY), 50.0, TIME_CREATED, "4", PERIOD, PERIOD_DESC),
                PeriodicalTransaction(0, CREDIT_CARD, Currency.RUB, IncomeTransactionsCategory(IncomeCategory.OTHER_INCOME), 50.0, TIME_CREATED, "5", PERIOD, PERIOD_DESC),
                PeriodicalTransaction(0, CREDIT_CARD, Currency.RUB, IncomeTransactionsCategory(IncomeCategory.OTHER_INCOME), 50.0, TIME_CREATED, "6", PERIOD, PERIOD_DESC)
        )
    }

}
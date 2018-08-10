package ru.popov.bodya.domain.transactions

import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import ru.popov.bodya.data.repositories.TransactionsRepository
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.ExpenseCategory
import ru.popov.bodya.domain.transactions.models.IncomeCategory
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType.CREDIT_CARD
import java.util.*

/**
 * @author popovbodya
 */
class TransactionsInteractorTest {

    private lateinit var transactionsInteractor: TransactionsInteractor
    private lateinit var transactionsRepository: TransactionsRepository

    @Before
    fun setUp() {
        transactionsRepository = mock(TransactionsRepository::class.java)
        transactionsInteractor = TransactionsInteractor(transactionsRepository)
    }

    @Test
    fun testGetAllTransactionsByWallet() {
        val expected = createStubTransactionList()
        `when`(transactionsRepository.getAllTransactionsByWallet(CREDIT_CARD)).thenReturn(Single.just(expected))
        transactionsInteractor.getAllTransactionsByWallet(CREDIT_CARD)
                .test()
                .assertValue(expected)
        verify(transactionsRepository).getAllTransactionsByWallet(CREDIT_CARD)
        verifyNoMoreInteractions(transactionsRepository)
    }

    @Test
    fun testGetIncomeTransactionsByWallet() {
        val expected = createStubIncomeTransactionList()
        `when`(transactionsRepository.getIncomeTransactionsByWallet(CREDIT_CARD)).thenReturn(Single.just(expected))
        transactionsInteractor.getIncomeTransactionsByWallet(CREDIT_CARD)
                .test()
                .assertValue(expected)
        verify(transactionsRepository).getIncomeTransactionsByWallet(CREDIT_CARD)
        verifyNoMoreInteractions(transactionsRepository)
    }

    @Test
    fun testGetExpenseTransactionsByWallet() {
        val expected = createStubExpenseTransactionList()
        `when`(transactionsRepository.getExpenseTransactionsByWallet(CREDIT_CARD)).thenReturn(Single.just(expected))
        transactionsInteractor.getExpenseTransactionsByWallet(CREDIT_CARD)
                .test()
                .assertValue(expected)
        verify(transactionsRepository).getExpenseTransactionsByWallet(CREDIT_CARD)
        verifyNoMoreInteractions(transactionsRepository)
    }

    @Test
    fun testAddIncomeTransaction() {
        val amount = 50.0
        val time = Calendar.getInstance().timeInMillis
        val desc = "gift"
        val category = TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.GIFT)
        val expectedParam = Transaction(0, CREDIT_CARD, Currency.EUR, category, amount, time, desc)
        `when`(transactionsRepository.addTransaction(expectedParam)).thenReturn(Completable.complete())
        transactionsInteractor.addTransaction(CREDIT_CARD, category, Currency.EUR, amount, time, desc)
                .test()
                .assertComplete()
    }

    @Test
    fun testAddExpenseTransaction() {
        val amount = 50.0
        val time = Calendar.getInstance().timeInMillis
        val desc = "food"
        val category = TransactionsCategory.ExpenseTransactionsCategory(ExpenseCategory.FOOD)
        val expectedParam = Transaction(0, CREDIT_CARD, Currency.USD, category, amount, time, desc)
        `when`(transactionsRepository.addTransaction(expectedParam)).thenReturn(Completable.complete())
        transactionsInteractor.addTransaction(CREDIT_CARD, category, Currency.USD, amount, time, desc)
                .test()
                .assertComplete()
    }

    private fun createStubTransactionList(): List<Transaction> {
        val list: MutableList<Transaction> = mutableListOf()
        list.addAll(createStubIncomeTransactionList())
        list.addAll(createStubExpenseTransactionList())
        return list
    }

    private fun createStubExpenseTransactionList(): List<Transaction> {
        val time = Calendar.getInstance().timeInMillis
        return listOf(
                Transaction(0, CREDIT_CARD, Currency.USD, TransactionsCategory.ExpenseTransactionsCategory(ExpenseCategory.FAMILY), 50.0, time, "1"),
                Transaction(0, CREDIT_CARD, Currency.USD, TransactionsCategory.ExpenseTransactionsCategory(ExpenseCategory.FAMILY), 25.0, time, "2")
        )
    }

    private fun createStubIncomeTransactionList(): List<Transaction> {
        val time = Calendar.getInstance().timeInMillis
        return listOf(
                Transaction(0, CREDIT_CARD, Currency.EUR, TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.GIFT), 30.0, time, "3"),
                Transaction(0, CREDIT_CARD, Currency.USD, TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.SALARY), 50.0, time, "4"),
                Transaction(0, CREDIT_CARD, Currency.RUB, TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.OTHER_INCOME), 50.0, time, "5"),
                Transaction(0, CREDIT_CARD, Currency.RUB, TransactionsCategory.IncomeTransactionsCategory(IncomeCategory.OTHER_INCOME), 50.0, time, "6")
        )
    }

}
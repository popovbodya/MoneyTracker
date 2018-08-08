package ru.popov.bodya.presentation.addtransaction

import android.arch.lifecycle.MutableLiveData
import ru.popov.bodya.core.mvwhatever.AppViewModel
import ru.popov.bodya.core.rx.RxSchedulersTransformer
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.PeriodicalTransactionsInteractor
import ru.popov.bodya.domain.transactions.TransactionsInteractor
import ru.popov.bodya.domain.transactions.models.ExpenseCategory
import ru.popov.bodya.domain.transactions.models.IncomeCategory
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.TransactionsCategory.Expense
import ru.popov.bodya.domain.transactions.models.TransactionsCategory.Income
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

/**
 *  @author popovbodya
 */
class AddTransactionViewModel @Inject constructor(private val transactionsInteractor: TransactionsInteractor,
                                                  private val periodicalTransactionsInteractor: PeriodicalTransactionsInteractor,
                                                  private val rxSchedulersTransformer: RxSchedulersTransformer,
                                                  private val router: Router) : AppViewModel() {

    companion object {
        private const val ONE_HOUR_IN_MS: Long = 3600000
        private const val ONE_DAY_IN_MS: Long = 86400000
    }

    val transactionCategoriesLiveData = MutableLiveData<List<TransactionsCategory>>()
    private var currentPeriod: Long = 0

    fun fetchTransactionCategories(isIncome: Boolean) {
        transactionCategoriesLiveData.value = when (isIncome) {
            true -> getAllIncomeTransaction()
            false -> getAllExpenseTransaction()
        }
    }

    fun onHomeButtonClicked() {
        router.exit()
    }

    fun onTransactionPeriodChanged(period: Int) {
        currentPeriod = when (period) {
            0 -> 0
            1 -> ONE_HOUR_IN_MS
            2 -> ONE_HOUR_IN_MS * 12
            3 -> ONE_DAY_IN_MS
            4 -> ONE_DAY_IN_MS * 2
            5 -> ONE_DAY_IN_MS * 7
            6 -> ONE_DAY_IN_MS * 30
            else -> 0
        }
    }

    fun onAddTransactionButtonClick(selectedWallet: WalletType, selectedCategory: TransactionsCategory, selectedCurrency: Currency, amount: Double, comment: String) {
        val currentTime = Calendar.getInstance().timeInMillis
        periodicalTransactionsInteractor.createTransaction(selectedWallet, selectedCategory, selectedCurrency, amount, currentTime, comment, currentPeriod)
                .compose(rxSchedulersTransformer.ioToMainTransformerCompletable())
                .subscribe(
                        { router.exit() },
                        { router.showSystemMessage("Transaction create failed") }
                )
    }

    private fun getAllIncomeTransaction(): ArrayList<Income> {
        val incomeList = arrayListOf<Income>()
        IncomeCategory.values().mapTo(incomeList) { Income(it) }
        return incomeList
    }

    private fun getAllExpenseTransaction(): ArrayList<Expense> {
        val expenseList = arrayListOf<Expense>()
        ExpenseCategory.values().mapTo(expenseList) { Expense(it) }
        return expenseList
    }
}



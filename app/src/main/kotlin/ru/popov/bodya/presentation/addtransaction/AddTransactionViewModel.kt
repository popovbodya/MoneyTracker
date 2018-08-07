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

    val transactionCategoriesLiveData = MutableLiveData<List<TransactionsCategory>>()

    fun fetchTransactionCategories(isIncome: Boolean) {
        transactionCategoriesLiveData.value = when (isIncome) {
            true -> getAllIncomeTransaction()
            false -> getAllExpenseTransaction()
        }
    }

    fun onHomeButtonClicked() {
        router.exit()
    }

    fun onAddTransactionButtonClick(selectedWallet: WalletType, selectedCategory: TransactionsCategory, selectedCurrency: Currency, amount: Double, comment: String) {
        val currentTime = Calendar.getInstance().timeInMillis
        periodicalTransactionsInteractor.createTransaction(selectedWallet, selectedCategory, selectedCurrency, amount, currentTime, comment, 60000)
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



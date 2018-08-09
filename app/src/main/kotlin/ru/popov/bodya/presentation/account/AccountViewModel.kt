package ru.popov.bodya.presentation.account

import android.arch.lifecycle.MutableLiveData
import com.lounah.moneytracker.data.entities.Resource
import com.lounah.wallettracker.R
import io.reactivex.SingleTransformer
import io.reactivex.functions.BiFunction
import ru.popov.bodya.core.extensions.connect
import ru.popov.bodya.core.mvwhatever.AppViewModel
import ru.popov.bodya.core.rx.RxSchedulers
import ru.popov.bodya.core.rx.RxSchedulersTransformer
import ru.popov.bodya.domain.calendar.CalendarInteractor
import ru.popov.bodya.domain.currency.CurrencyInteractor
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.currency.model.CurrencyAmount
import ru.popov.bodya.domain.currency.model.Rates
import ru.popov.bodya.domain.transactions.PeriodicalTransactionsInteractor
import ru.popov.bodya.domain.transactions.TransactionsInteractor
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.popov.bodya.presentation.common.Screens.STATISTICS_SCREEN
import ru.popov.bodya.presentation.statistics.model.StatisticsInitialData
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AccountViewModel @Inject constructor(
        private val router: Router,
        private val calendarInteractor: CalendarInteractor,
        private val transactionsInteractor: TransactionsInteractor,
        private val periodicalTransactionsInteractor: PeriodicalTransactionsInteractor,
        private val currencyInteractor: CurrencyInteractor,
        private val rxSchedulers: RxSchedulers,
        private val rxSchedulersTransformer: RxSchedulersTransformer) : AppViewModel() {

    val currentBalanceLiveData = MutableLiveData<CurrencyAmount>()
    val transactionsLiveData = MutableLiveData<Resource<List<Transaction>>>()
    val incomeLiveData = MutableLiveData<Double>()
    val expenseLiveData = MutableLiveData<Double>()
    val usdExchangeRateLiveData = MutableLiveData<Resource<Double>>()
    val eurExchangeRateLiveData = MutableLiveData<Resource<Double>>()

    private var currentCurrency = Currency.RUB
    private var currentWallet = WalletType.CASH


    fun fetchAccountData() {
        val currentTime = Calendar.getInstance().timeInMillis
        periodicalTransactionsInteractor.getAllPeriodicalTransactionsByWallet(currentWallet)
                .map { periodicalTransactionsInteractor.createTransactionListBasedOnPeriodicalTransactions(it, currentTime) }
                .doOnSuccess { transactionsInteractor.addTransactionList(it) }
                .flatMap { calendarInteractor.getCurrentMonthTransactions(currentWallet, currentTime) }
                .doOnSuccess { transactionsLiveData.postValue(Resource.success(it)) }
                .compose(fetchTransactionsBasedData())
                .subscribe { amount -> currentBalanceLiveData.postValue(CurrencyAmount(amount, currentCurrency)) }
                .connect(compositeDisposable)
    }

    fun onCurrencyRadioGroupClick(radioButtonId: Int) {
        currentCurrency = when (radioButtonId) {
            R.id.usd_radio_button -> Currency.USD
            R.id.euro_radio_button -> Currency.EUR
            else -> Currency.RUB
        }
        fetchAccountData()
    }

    fun onIncomeBalanceClick() {
        val currentTime = Calendar.getInstance().timeInMillis
        router.navigateTo(STATISTICS_SCREEN, StatisticsInitialData(currentTime, currentWallet, true))
    }

    fun onExpenseBalanceClick() {
        val currentTime = Calendar.getInstance().timeInMillis
        router.navigateTo(STATISTICS_SCREEN, StatisticsInitialData(currentTime, currentWallet, false))
    }

    fun onWalletChanged(walletType: WalletType) {
        currentWallet = walletType
        fetchAccountData()
    }

    fun onTransactionDeleted(transaction: Transaction) {
        val currentTime = Calendar.getInstance().timeInMillis
        transactionsInteractor.removeTransaction(transaction)
                .toSingle { true }
                .flatMap { calendarInteractor.getCurrentMonthTransactions(currentWallet, currentTime) }
                .compose(fetchTransactionsBasedData())
                .subscribe { amount -> currentBalanceLiveData.postValue(CurrencyAmount(amount, currentCurrency)) }
                .connect(compositeDisposable)
    }

    private fun fetchTransactionsBasedData(): SingleTransformer<List<Transaction>, Double> {
        return SingleTransformer {
            it.observeOn(rxSchedulers.computationScheduler())
                    .map { transactionsInteractor.getTransactionsPair(it) }
                    .zipWith(
                            currencyInteractor.getCachedExchangeRate().doOnSuccess { rates: Rates ->
                                usdExchangeRateLiveData.postValue(Resource.success(currencyInteractor.getUsdRate(rates.usd)))
                                eurExchangeRateLiveData.postValue(Resource.success(currencyInteractor.getEurRate(rates.eur)))
                            },
                            BiFunction { pair: Pair<List<Transaction>, List<Transaction>>, rates: Rates ->
                                Timber.d("${Thread.currentThread()}")
                                getListPairAmounts(pair, rates)
                            })
                    .doOnSuccess {
                        incomeLiveData.postValue(it.first)
                        expenseLiveData.postValue(it.second)
                    }
                    .map { transactionsInteractor.getPairDiff(it) }
                    .compose(rxSchedulersTransformer.ioToMainTransformerSingle())
                    .doOnSubscribe { usdExchangeRateLiveData.postValue(Resource.loading(0.0)) }
        }
    }

    private fun getListPairAmounts(pair: Pair<List<Transaction>, List<Transaction>>, rates: Rates): Pair<Double, Double> =
            Pair(getTransactionListAmountWithCurrency(pair.first, rates), getTransactionListAmountWithCurrency(pair.second, rates))

    private fun getTransactionListAmountWithCurrency(transactionList: List<Transaction>, rates: Rates): Double {
        val transactionListAmountInRubles = transactionList.sumByDouble { getTransactionAmount(it, rates) }
        return when (currentCurrency) {
            Currency.RUB -> transactionListAmountInRubles
            Currency.EUR -> transactionListAmountInRubles * rates.eur
            Currency.USD -> transactionListAmountInRubles * rates.usd
        }
    }

    private fun getTransactionAmount(transaction: Transaction, rates: Rates): Double {
        return when (transaction.currency) {
            Currency.RUB -> transaction.amount
            Currency.USD -> transaction.amount * (1 / rates.usd)
            Currency.EUR -> transaction.amount * (1 / rates.eur)
        }
    }
}
package ru.popov.bodya.presentation.account

import android.arch.lifecycle.MutableLiveData
import com.lounah.moneytracker.data.entities.Resource
import com.lounah.wallettracker.R
import io.reactivex.functions.BiFunction
import ru.popov.bodya.core.extensions.connect
import ru.popov.bodya.core.mvwhatever.AppViewModel
import ru.popov.bodya.core.rx.RxSchedulers
import ru.popov.bodya.core.rx.RxSchedulersTransformer
import ru.popov.bodya.domain.currency.CurrencyInteractor
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.currency.model.Rates
import ru.popov.bodya.domain.transactions.TransactionsInteractor
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType
import javax.inject.Inject

class AccountViewModel @Inject constructor(
        private val transactionsInteractor: TransactionsInteractor,
        private val currencyInteractor: CurrencyInteractor,
        private val rxSchedulers: RxSchedulers,
        private val rxSchedulersTransformer: RxSchedulersTransformer) : AppViewModel() {

    val currentBalanceLiveData = MutableLiveData<Resource<Double>>()
    val transactionsLiveData = MutableLiveData<Resource<List<Transaction>>>()
    val incomeLiveData = MutableLiveData<Double>()
    val expenseLiveData = MutableLiveData<Double>()
    val usdExchangeRateLiveData = MutableLiveData<Resource<Double>>()
    val eurExchangeRateLiveData = MutableLiveData<Resource<Double>>()

    private var currentCurrency = Currency.RUB

    fun fetchInitialData() {

        currencyInteractor.getCachedExchangeRate()
                .doOnSubscribe { usdExchangeRateLiveData.postValue(Resource.loading(0.0)) }
                .doOnSuccess { rates: Rates ->
                    usdExchangeRateLiveData.postValue(Resource.success(currencyInteractor.getUsdRate(rates.usd)))
                    eurExchangeRateLiveData.postValue(Resource.success(currencyInteractor.getEurRate(rates.eur)))
                }
                .doOnError { usdExchangeRateLiveData.postValue(Resource.error("", 0.0)) }
                .flatMap { transactionsInteractor.getAllTransactionsByWallet(WalletType.BANK_ACCOUNT) }
                .doOnSuccess { transactionsLiveData.postValue(Resource.success(it)) }
                .observeOn(rxSchedulers.computationScheduler())
                .map { getTransactionsPair(it) }
                .zipWith(currencyInteractor.getCachedExchangeRate(),
                        BiFunction { pair: Pair<List<Transaction>, List<Transaction>>, rates: Rates ->
                            getListPairAmounts(pair, rates)
                        })
                .doOnSuccess {
                    incomeLiveData.postValue(it.first)
                    expenseLiveData.postValue(it.second)
                }
                .map { getAmount(it) }
                .compose(rxSchedulersTransformer.ioToMainTransformerSingle())
                .subscribe { amount -> currentBalanceLiveData.postValue(Resource.success(amount)) }
                .connect(compositeDisposable)
    }

    fun onCurrencyRadioGroupClick(radioButtonId: Int) {
        currentCurrency = when (radioButtonId) {
            R.id.usd_radio_button -> Currency.USD
            R.id.euro_radio_button -> Currency.EUR
            else -> Currency.RUB
        }
        fetchInitialData()
    }

    private fun getAmount(pair: Pair<Double, Double>): Double = pair.first - pair.second

    private fun getListPairAmounts(pair: Pair<List<Transaction>, List<Transaction>>, rates: Rates): Pair<Double, Double> =
            Pair(getTransactionListAmountWithCurrency(pair.first, rates), getTransactionListAmountWithCurrency(pair.second, rates))

    private fun getTransactionsPair(transactionList: List<Transaction>): Pair<List<Transaction>, List<Transaction>> {
        val incomeList: MutableList<Transaction> = mutableListOf()
        val exposeList: MutableList<Transaction> = mutableListOf()
        transactionList.forEach { transaction ->
            when (transaction.category) {
                is TransactionsCategory.Expense -> exposeList.add(transaction)
                is TransactionsCategory.Income -> incomeList.add(transaction)
            }
        }
        return Pair(incomeList, exposeList)
    }

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
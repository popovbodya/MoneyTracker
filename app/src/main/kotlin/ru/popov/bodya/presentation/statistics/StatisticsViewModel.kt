package ru.popov.bodya.presentation.statistics

import android.arch.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.lounah.moneytracker.data.entities.Resource
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import ru.popov.bodya.core.mvwhatever.AppViewModel
import ru.popov.bodya.core.rx.RxSchedulers
import ru.popov.bodya.core.rx.RxSchedulersTransformer
import ru.popov.bodya.domain.calendar.CalendarInteractor
import ru.popov.bodya.domain.statistics.StatisticsInteractor
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 *  @author popovbodya
 */
class StatisticsViewModel @Inject constructor(private val calendarInteractor: CalendarInteractor,
                                              private val statisticsInteractor: StatisticsInteractor,
                                              private val schedulers: RxSchedulers,
                                              private val rxSchedulersTransformer: RxSchedulersTransformer,
                                              private val router: Router) : AppViewModel() {

    val transactionsLiveData = MutableLiveData<List<Transaction>>()
    val pieDataSetLiveData = MutableLiveData<PieData>()

    fun fetchCurrentMonthTransactions(isIncome: Boolean, walletType: WalletType, currentDate: Long) {

        val transactionsSingle: Single<List<Transaction>> = when (isIncome) {
            true -> calendarInteractor.getCurrentMonthIncomeTransactions(walletType, currentDate)
            false -> calendarInteractor.getCurrentMonthExpenseTransactions(walletType, currentDate)
        }

        transactionsSingle
                .doOnSuccess { transactionsLiveData.postValue(it) }
                .observeOn(schedulers.computationScheduler())
                .flatMap { statisticsInteractor.createPieDataSetBasedOnTransactionCategoriesSingle(it) }
                .compose(rxSchedulersTransformer.ioToMainTransformerSingle())
                .subscribe(Consumer { pieDataSetLiveData.postValue(it) })
    }

    fun onHomeButtonClick() {
        router.exit()
    }
}
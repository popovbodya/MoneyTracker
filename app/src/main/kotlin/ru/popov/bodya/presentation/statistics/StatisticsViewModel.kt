package ru.popov.bodya.presentation.statistics

import android.arch.lifecycle.MutableLiveData
import com.lounah.moneytracker.data.entities.Resource
import io.reactivex.functions.Consumer
import ru.popov.bodya.core.mvwhatever.AppViewModel
import ru.popov.bodya.core.rx.RxSchedulersTransformer
import ru.popov.bodya.domain.calendar.CalendarInteractor
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.WalletType
import javax.inject.Inject

/**
 *  @author popovbodya
 */
class StatisticsViewModel @Inject constructor(private val calendarInteractor: CalendarInteractor,
                                              private val rxSchedulersTransformer: RxSchedulersTransformer) : AppViewModel() {

    val transactionsLiveData = MutableLiveData<List<Transaction>>()

    fun fetchCurrentMonthTransactions(isIncome: Boolean, walletType: WalletType, currentDate: Long) {
        calendarInteractor.getCurrentMonthTransactions(walletType, currentDate)
                .compose(rxSchedulersTransformer.ioToMainTransformerSingle())
                .subscribe(Consumer { transactionsLiveData.postValue(it) })
    }

}
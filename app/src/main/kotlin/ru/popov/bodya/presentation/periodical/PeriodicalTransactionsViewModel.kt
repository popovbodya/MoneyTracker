package ru.popov.bodya.presentation.periodical

import android.arch.lifecycle.MutableLiveData
import io.reactivex.functions.Consumer
import ru.popov.bodya.core.mvwhatever.AppViewModel
import ru.popov.bodya.core.rx.RxSchedulersTransformer
import ru.popov.bodya.data.repositories.PeriodicalTransactionsRepository
import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 *  @author popovbodya
 */
class PeriodicalTransactionsViewModel @Inject constructor(private val periodicalTransactionsRepository: PeriodicalTransactionsRepository,
                                                          private val router: Router,
                                                          private val rxSchedulersTransformer: RxSchedulersTransformer) : AppViewModel() {

    val periodicalTransactionsLiveData = MutableLiveData<List<PeriodicalTransaction>>()

    fun fetchAllPeriodicalTransactions(walletType: WalletType) {
        periodicalTransactionsRepository.getAllPeriodicalTransactionsByWallet(walletType)
                .compose(rxSchedulersTransformer.ioToMainTransformerSingle())
                .subscribe(Consumer { periodicalTransactionsLiveData.postValue(it) })
    }

    fun onPeriodicalTransactionDeleted(walletType: WalletType, periodicalTransaction: PeriodicalTransaction) {
        periodicalTransactionsRepository.deletePeriodicalTransaction(walletType, periodicalTransaction)
                .compose(rxSchedulersTransformer.ioToMainTransformerCompletable())
                .subscribe { router.showSystemMessage("Transaction delete") }
    }

    fun onHomeButtonClicked() {
        router.exit()
    }

}
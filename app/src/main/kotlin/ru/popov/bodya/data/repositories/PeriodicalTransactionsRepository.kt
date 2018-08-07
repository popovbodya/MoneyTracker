package ru.popov.bodya.data.repositories

import io.reactivex.Completable
import io.reactivex.Single
import ru.popov.bodya.data.database.transactions.converters.models.PeriodicalTransactionEntityConverter
import ru.popov.bodya.data.database.transactions.dao.PeriodicalTransactionsDao
import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 * @author popovbodya
 */
class PeriodicalTransactionsRepository(private val periodicalTransactionsDao: PeriodicalTransactionsDao,
                                       private val periodicalTransactionEntityConverter: PeriodicalTransactionEntityConverter) {

    fun getAllPeriodicalTransactionsByWallet(walletType: WalletType): Single<List<PeriodicalTransaction>> =
            Single.fromCallable {
                periodicalTransactionsDao.getAllTransactionsByWallet(walletType)
                        .map { periodicalTransactionEntityConverter.reverse(it) }
            }

    fun addPeriodicalTransaction(periodicalTransaction: PeriodicalTransaction): Completable =
            Completable.fromAction { periodicalTransactionsDao.insert(periodicalTransactionEntityConverter.convert(periodicalTransaction)) }

    fun modifyPeriodicalTransactionTimeUpdated(transactionId: Long, timeUpdated: Long) {
        periodicalTransactionsDao.updatePeriodicalTransactionTimeUpdated(transactionId, timeUpdated)
    }

}
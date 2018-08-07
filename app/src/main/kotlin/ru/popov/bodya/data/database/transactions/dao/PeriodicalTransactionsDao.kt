package ru.popov.bodya.data.database.transactions.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ru.popov.bodya.data.database.BaseDao
import ru.popov.bodya.data.database.transactions.entities.PeriodicalTransactionEntity
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 * @author popovbodya
 */
@Dao
interface PeriodicalTransactionsDao : BaseDao<PeriodicalTransactionEntity> {

    @Query("SELECT * FROM periodicalTransactions WHERE wallet=:wallet")
    fun getAllTransactionsByWallet(wallet: WalletType): List<PeriodicalTransactionEntity>

    @Query("UPDATE periodicalTransactions SET timeUpdated = :timeUpdated where id = :transactionId")
    fun updatePeriodicalTransactionTimeUpdated(transactionId: Long, timeUpdated: Long)

}
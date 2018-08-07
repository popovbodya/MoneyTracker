package ru.popov.bodya.data.database.transactions.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 * @author popovbodya
 */
@Entity(tableName = "periodicalTransactions")
data class PeriodicalTransactionEntity(@PrimaryKey(autoGenerate = true)
                                       var id: Long,
                                       var wallet: WalletType,
                                       val currency: Currency,
                                       val category: TransactionsCategory,
                                       val amount: Double,
                                       val timeUpdated: Long,
                                       val description: String,
                                       val period: Long)



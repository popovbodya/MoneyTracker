package ru.popov.bodya.data.database.transactions.converters.models

import android.util.Log
import ru.popov.bodya.core.converter.Converter
import ru.popov.bodya.data.database.transactions.entities.PeriodicalTransactionEntity
import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction

/**
 * @author popovbodya
 */
class PeriodicalTransactionEntityConverter : Converter<PeriodicalTransaction, PeriodicalTransactionEntity> {
    override fun reverse(to: PeriodicalTransactionEntity): PeriodicalTransaction  {
        Log.e("bodya", "reverse")
        return PeriodicalTransaction(to.id, to.wallet, to.currency, to.category, to.amount, to.timeUpdated, to.description, to.period, to.periodDescription)
    }

    override fun convert(from: PeriodicalTransaction): PeriodicalTransactionEntity =
            PeriodicalTransactionEntity(from.id, from.wallet, from.currency, from.category, from.amount, from.timeCreated, from.description, from.period, from.periodDescription)
}
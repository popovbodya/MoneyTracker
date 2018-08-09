package ru.popov.bodya.domain.calendar


import io.reactivex.Single
import ru.popov.bodya.data.repositories.TransactionsRepository
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.WalletType
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH

/**
 *  @author popovbodya
 */
class CalendarInteractor(private val transactionsRepository: TransactionsRepository) {

    fun getCurrentMonthTransactions(walletType: WalletType, currentDate: Long): Single<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDate
        calendar.set(DAY_OF_MONTH, 1)
        val startDate = calendar.timeInMillis
        calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(MONTH))
        val endDate = calendar.timeInMillis
        return transactionsRepository
                .getTransactionsByWalletAndDate(walletType, startDate, endDate)
    }

    fun getNextMonthTransactions(walletType: WalletType, currentDate: Long): Single<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDate
        calendar.add(MONTH, 1)
        val startDate = calendar.timeInMillis
        calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(MONTH))
        val endDate = calendar.timeInMillis
        return transactionsRepository
                .getTransactionsByWalletAndDate(walletType, startDate, endDate)
    }

    fun getPreviousMonthTransactions(walletType: WalletType, currentDate: Long): Single<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDate
        calendar.add(MONTH, -1)
        calendar.set(DAY_OF_MONTH, 1)
        val startDate = calendar.timeInMillis
        calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(MONTH))
        val endDate = calendar.timeInMillis
        return transactionsRepository
                .getTransactionsByWalletAndDate(walletType, startDate, endDate)
    }
}
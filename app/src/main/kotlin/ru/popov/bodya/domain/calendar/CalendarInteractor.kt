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

    fun getCurrentMonthTransactions(walletType: WalletType, currentTime: Long): Single<List<Transaction>> {
        val calendar = Calendar.getInstance()
        return transactionsRepository
                .getTransactionsByWalletAndDate(walletType, getFirstMonthDay(currentTime, calendar), getLastMonthDay(currentTime, calendar))
    }

    fun getCurrentMonthIncomeTransactions(walletType: WalletType, currentTime: Long): Single<List<Transaction>> {
        val calendar = Calendar.getInstance()
        return transactionsRepository
                .getIncomeTransactionsByWalletAndDate(walletType, getFirstMonthDay(currentTime, calendar), getLastMonthDay(currentTime, calendar))
    }

    fun getCurrentMonthExpenseTransactions(walletType: WalletType, currentTime: Long): Single<List<Transaction>> {
        val calendar = Calendar.getInstance()
        return transactionsRepository
                .getExpenseTransactionsByWalletAndDate(walletType, getFirstMonthDay(currentTime, calendar), getLastMonthDay(currentTime, calendar))
    }

    private fun getFirstMonthDay(currentTime: Long, calendar: Calendar): Long {
        calendar.timeInMillis = currentTime
        calendar.set(DAY_OF_MONTH, 1)
        return calendar.timeInMillis
    }

    private fun getLastMonthDay(currentTime: Long, calendar: Calendar): Long {
        calendar.timeInMillis = currentTime
        calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(MONTH))
        return calendar.timeInMillis
    }
}
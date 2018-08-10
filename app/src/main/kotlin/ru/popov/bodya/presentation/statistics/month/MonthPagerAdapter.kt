package ru.popov.bodya.presentation.statistics.month

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import ru.popov.bodya.domain.transactions.models.WalletType
import java.util.*

/**
 *  @author popovbodya
 */
class MonthPagerAdapter(fragmentManager: FragmentManager, private val walletType: WalletType) : FragmentStatePagerAdapter(fragmentManager) {

    companion object {
        const val MONTHS_IN_A_YEAR = 12
        const val COUNT = 24
        const val INITIAL_VALUE = 12
    }

    override fun getCount(): Int = COUNT

    override fun getItem(position: Int): Fragment {
        val currentPositionMonthTime = getCurrentPositionMonthTime(position)
        return MonthTransactionsFragment.newInstance(currentPositionMonthTime)
    }

    private fun getCurrentPositionMonthTime(currentMonthPosition: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, currentMonthPosition - MONTHS_IN_A_YEAR)
        return calendar.timeInMillis
    }

}
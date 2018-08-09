package ru.popov.bodya.presentation.statistics

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lounah.wallettracker.R
import kotlinx.android.synthetic.main.current_month_layout.*
import java.text.SimpleDateFormat
import java.util.*


/**
 *  @author popovbodya
 */
class MonthTransactionsFragment : Fragment() {

    companion object {

        private const val DEFAULT_TIME_VALUE = 0L
        private const val CURRENT_TIME = "currentTime"

        fun newInstance(currentTime: Long): MonthTransactionsFragment {
            val args = Bundle()
            args.putLong(CURRENT_TIME, currentTime)
            val fragment = MonthTransactionsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var currentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentTimeLong = arguments?.getLong(CURRENT_TIME, DEFAULT_TIME_VALUE)
                ?: DEFAULT_TIME_VALUE
        currentDate = Date(currentTimeLong)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.current_month_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        current_month_title_text_view.text = SimpleDateFormat("MMMM y", Locale.ENGLISH).format(currentDate)
    }
}
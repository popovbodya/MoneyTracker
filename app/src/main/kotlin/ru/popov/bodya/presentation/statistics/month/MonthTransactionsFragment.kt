package ru.popov.bodya.presentation.statistics.month

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.current_month_layout.*
import ru.popov.bodya.R
import ru.popov.bodya.core.mvwhatever.AppFragment
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


/**
 *  @author popovbodya
 */
class MonthTransactionsFragment : AppFragment() {

    companion object {

        private const val DATE_FORMAT = "MMMM y"
        private const val DEFAULT_TIME_VALUE = 0L
        private const val CURRENT_TIME_BUNDLE_KEY = "currentTime"

        fun newInstance(currentTime: Long): MonthTransactionsFragment {
            val args = Bundle()
            args.putLong(CURRENT_TIME_BUNDLE_KEY, currentTime)
            val fragment = MonthTransactionsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var currentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentTimeLong = arguments?.getLong(CURRENT_TIME_BUNDLE_KEY, DEFAULT_TIME_VALUE)
                ?: DEFAULT_TIME_VALUE
        currentDate = Date(currentTimeLong)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.current_month_layout, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            val russianLocal = Locale("ru", "RU")
            current_month_title_text_view.text = when (russianLocal) {
                getCurrentLocale(it) -> SimpleDateFormat(DATE_FORMAT, getRussianDateFormatSymbols()).format(currentDate)
                else -> SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(currentDate)
            }
        }
    }

    private fun getRussianDateFormatSymbols(): DateFormatSymbols {
        return object : DateFormatSymbols() {
            override fun getMonths(): Array<String> {
                return arrayOf("январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь")
            }
        }
    }

    private fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.getResources().configuration.locale
        }
    }
}
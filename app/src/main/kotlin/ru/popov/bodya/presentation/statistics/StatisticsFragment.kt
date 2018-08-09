package ru.popov.bodya.presentation.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.lounah.wallettracker.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.statistics_fragment_layout.*
import ru.popov.bodya.core.mvwhatever.AppFragment
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.popov.bodya.presentation.common.translatedNameId
import ru.popov.bodya.presentation.statistics.MonthPagerAdapter.Companion.INITIAL_VALUE
import ru.popov.bodya.presentation.statistics.model.StatisticsInitialData
import ru.popov.bodya.presentation.transactions.TransactionsRecyclerAdapter
import java.util.*
import javax.inject.Inject

/**
 *  @author popovbodya
 */
class StatisticsFragment : AppFragment() {

    companion object {

        private const val DEFAULT_TIME_VALUE = 0L
        private const val CURRENT_TIME = "currentTime"
        private const val CURRENT_WALLET = "currentWallet"
        private const val IS_INCOME = "isIncome"

        fun newInstance(data: StatisticsInitialData): StatisticsFragment {
            val args = Bundle()
            args.putLong(CURRENT_TIME, data.currentTime)
            args.putSerializable(CURRENT_WALLET, data.currentWallet)
            args.putBoolean(IS_INCOME, data.isIncome)
            val fragment = StatisticsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var viewModel: StatisticsViewModel

    private lateinit var pagerAdapter: MonthPagerAdapter
    private lateinit var transactionsAdapter: TransactionsRecyclerAdapter

    private lateinit var currentDate: Date
    private lateinit var currentWallet: WalletType
    private var isIncome: Boolean = false

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.let {
            val currentTimeLong = it.getLong(CURRENT_TIME, DEFAULT_TIME_VALUE)
            currentDate = Date(currentTimeLong)
            currentWallet = it.getSerializable(CURRENT_WALLET) as? WalletType ?: WalletType.CASH
            isIncome = it.getBoolean(IS_INCOME)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val parentView: View = inflater.inflate(R.layout.statistics_fragment_layout, container, false)
        initToolbar(parentView)
        return parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapters()
        viewModel.fetchCurrentMonthTransactions(isIncome, currentWallet, getCurrentPositionMonthTime(INITIAL_VALUE))
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModel()
    }

    override fun onStop() {
        super.onStop()
        viewModel.transactionsLiveData.removeObservers(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.onHomeButtonClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initToolbar(parentView: View) {
        setHasOptionsMenu(true)
        val activity = activity as AppCompatActivity
        val toolbar = parentView.findViewById<Toolbar>(R.id.toolbar)
        val walletName = getString(currentWallet.translatedNameId())
        toolbar.title = when (isIncome) {
            true -> String.format(Locale.ENGLISH, getString(R.string.statistic_income_title), walletName)
            false -> String.format(Locale.ENGLISH, getString(R.string.statistic_expense_title), walletName)
        }
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapters() {
        transactionsAdapter = TransactionsRecyclerAdapter()
        transactions_recycler_view.adapter = transactionsAdapter
        transactions_recycler_view.layoutManager = LinearLayoutManager(context)
        transactions_recycler_view.isNestedScrollingEnabled = false

        pagerAdapter = MonthPagerAdapter(childFragmentManager, currentWallet)
        month_view_pager.adapter = pagerAdapter
        month_view_pager.setCurrentItem(INITIAL_VALUE, false)
        month_view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                viewModel.fetchCurrentMonthTransactions(isIncome, currentWallet, getCurrentPositionMonthTime(position))
            }
        })
        month_view_pager.clipToPadding = false
        month_view_pager.setPadding(192, 0, 192, 0)
        month_view_pager.pageMargin = 36
    }

    private fun getCurrentPositionMonthTime(currentMonthPosition: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, currentMonthPosition - INITIAL_VALUE)
        return calendar.timeInMillis
    }

    private fun subscribeToViewModel() {
        viewModel.transactionsLiveData.observe(this, Observer { transactions ->
            transactions?.let { processSuccessTransactionsResponse(it) }
        })

        viewModel.pieDataSetLiveData.observe(this, Observer { pieData ->
            pieData?.let {
                val description = Description()
                description.text = toolbar.title.toString()
                chart.data = pieData
                chart.description = description
                chart.isRotationEnabled = false
                chart.setUsePercentValues(false)
                chart.visibility = View.VISIBLE
                chart.animateY(1400)
            }
        })
    }

    private fun processSuccessTransactionsResponse(data: List<Transaction>) {
        transactionsAdapter.updateDataSet(data)
    }

}
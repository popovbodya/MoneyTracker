package ru.popov.bodya.presentation.periodical

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.periodical_transactions_fragment.*
import kotlinx.android.synthetic.main.viewer_zero_layout.*
import ru.popov.bodya.R
import ru.popov.bodya.core.mvwhatever.AppFragment
import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.popov.bodya.presentation.common.SwipeToDeleteCallback
import ru.popov.bodya.presentation.periodical.adapter.OnPeriodicalTransactionDeletedListener
import ru.popov.bodya.presentation.periodical.adapter.PeriodicalTransactionsAdapter
import javax.inject.Inject

/**
 *  @author popovbodya
 */
class PeriodicalTransactionsFragment : AppFragment(), OnPeriodicalTransactionDeletedListener {

    companion object {

        private const val CURRENT_WALLET = "currentWallet"

        fun newInstance(currentWallet: WalletType): PeriodicalTransactionsFragment {
            val args = Bundle()
            args.putSerializable(CURRENT_WALLET, currentWallet)
            val fragment = PeriodicalTransactionsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var viewModel: PeriodicalTransactionsViewModel

    private lateinit var currentWallet: WalletType
    private lateinit var periodicalTransactionsAdapter: PeriodicalTransactionsAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.let {
            currentWallet = it.getSerializable(CURRENT_WALLET) as? WalletType ?: WalletType.CASH
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parentView = inflater.inflate(R.layout.periodical_transactions_fragment, container, false)
        initToolbar(parentView)
        return parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTransactionsList()
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModel()
        viewModel.fetchAllPeriodicalTransactions(currentWallet)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.onHomeButtonClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPeriodicalTransactionDeleted(periodicalTransaction: PeriodicalTransaction) {
        viewModel.onPeriodicalTransactionDeleted(currentWallet, periodicalTransaction)
    }

    private fun subscribeToViewModel() {
        viewModel.periodicalTransactionsLiveData.observe(this, Observer { transactionList ->
            transactionList?.let {
                if (!it.isEmpty()) {
                    showEmptyData(false)
                    periodicalTransactionsAdapter.updateDataSet(it)
                } else {
                    showEmptyData(true)
                }
            }
        })
    }

    private fun showEmptyData(show: Boolean) {
        zero_layout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun initTransactionsList() {
        periodicalTransactionsAdapter = PeriodicalTransactionsAdapter(this)
        transactions_recycler_view.adapter = periodicalTransactionsAdapter
        transactions_recycler_view.layoutManager = LinearLayoutManager(context)
        transactions_recycler_view.itemAnimator = DefaultItemAnimator()

        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = transactions_recycler_view.adapter as PeriodicalTransactionsAdapter
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(transactions_recycler_view)
    }

    private fun initToolbar(parentView: View) {
        setHasOptionsMenu(true)
        val toolbar = parentView.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.periodic_transactions_title)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
package ru.popov.bodya.presentation.account

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_wallet.*
import ru.popov.bodya.R
import ru.popov.bodya.core.mvwhatever.AppFragment
import ru.popov.bodya.domain.common.model.Status
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.currency.model.CurrencyAmount
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.popov.bodya.presentation.common.Screens.ADD_NEW_TRANSACTION_SCREEN
import ru.popov.bodya.presentation.common.SwipeToDeleteCallback
import ru.popov.bodya.presentation.transactions.OnTransactionDeletedListener
import ru.popov.bodya.presentation.transactions.TransactionsRecyclerAdapter
import ru.terrakok.cicerone.Router
import java.text.DecimalFormat
import javax.inject.Inject


class AccountFragment : AppFragment(), OnTransactionDeletedListener {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var viewModel: AccountViewModel

    private val formatter = DecimalFormat("#0.00")
    private lateinit var transactionsAdapter: TransactionsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parentView = inflater.inflate(R.layout.fragment_wallet, container, false)
        setHasOptionsMenu(true)
        initToolbar(parentView)
        return parentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModel()
        viewModel.fetchAccountData()
    }

    override fun onResume() {
        super.onResume()
        fab_add.collapse()
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.account_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cashWallet -> {
                viewModel.onWalletChanged(WalletType.CASH)
                changeToolbarTitle(getString(R.string.wallet))
                return true
            }
            R.id.creditWallet -> {
                viewModel.onWalletChanged(WalletType.CREDIT_CARD)
                changeToolbarTitle(getString(R.string.credit_card))
                return true
            }
            R.id.bankWallet -> {
                viewModel.onWalletChanged(WalletType.BANK_ACCOUNT)
                changeToolbarTitle(getString(R.string.bank_account))
                return true
            }
            R.id.periodical_transactions -> {
                viewModel.onPeriodicalTransactionsMenuItemClicked()
                return true
            }
            R.id.about -> {
                viewModel.onAboutMenuItemClicked()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTransactionDeleted(transaction: Transaction) {
        viewModel.onTransactionDeleted(transaction)
    }

    private fun initUI() {
        initFab()
        initCurrenciesRadioGroup()
        initTransactionsList()
        initCardViews()
    }

    private fun initCardViews() {
        incomes_card_view.setOnClickListener { viewModel.onIncomeBalanceClick() }
        expenses_card_view.setOnClickListener { viewModel.onExpenseBalanceClick() }
    }

    private fun initToolbar(parentView: View) {
        val toolbar = parentView.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
    }

    private fun changeToolbarTitle(title: String) {
        toolbar.title = title
    }

    private fun initFab() {
        fab_new_income.setOnClickListener { addNewIncome() }
        fab_new_expense.setOnClickListener { addNewExpense() }
    }

    private fun addNewIncome() {
        router.navigateTo(ADD_NEW_TRANSACTION_SCREEN, true)
    }

    private fun addNewExpense() {
        router.navigateTo(ADD_NEW_TRANSACTION_SCREEN, false)
    }

    private fun initCurrenciesRadioGroup() {
        rg_currencies.check(R.id.rub_radio_button)
        rg_currencies.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onCurrencyRadioGroupClick(checkedId)
        }
    }

    private fun initTransactionsList() {
        transactionsAdapter = TransactionsRecyclerAdapter(this)
        transactions_recycler_view.adapter = transactionsAdapter
        transactions_recycler_view.layoutManager = LinearLayoutManager(context)
        transactions_recycler_view.itemAnimator = DefaultItemAnimator()
        transactions_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 10 && fab_add.visibility == View.VISIBLE) {
                    fab_add.collapse()
                    fab_add.visibility = View.GONE
                    return
                }
                if (dy < -10 && fab_add.visibility != View.VISIBLE) {
                    fab_add.visibility = View.VISIBLE
                    return
                }
            }
        })

        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = transactions_recycler_view.adapter as TransactionsRecyclerAdapter
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(transactions_recycler_view)
    }

    private fun subscribeToViewModel() {
        viewModel.currentBalanceLiveData.observe(this, Observer { currencyAmount -> currencyAmount?.let { showCurrencyAmount(currencyAmount) } })

        viewModel.transactionsLiveData.observe(this, Observer { resource ->
            when (resource?.status) {
                Status.SUCCESS -> resource.data?.let { processSuccessTransactionsResponse(it) }
                Status.LOADING -> {
                }
                Status.ERROR -> processErrorState()
            }
        })

        viewModel.incomeLiveData.observe(this, Observer { amount ->
            amount?.let { incomes_text_view.amount = amount.toFloat() }
        })

        viewModel.expenseLiveData.observe(this, Observer { amount ->
            amount?.let { expenses_text_view.amount = amount.toFloat() }
        })
        viewModel.usdExchangeRateLiveData.observe(this, Observer { response ->
            when (response?.status) {
                Status.ERROR -> processErrorState()
                Status.SUCCESS -> processSuccessFirstExchangeRate(response.data)
                Status.LOADING -> {
                }
            }
        })

        viewModel.eurExchangeRateLiveData.observe(this, Observer { response ->
            when (response?.status) {
                Status.ERROR -> processErrorState()
                Status.SUCCESS -> processSuccessSecondExchangeRate(response.data)
                Status.LOADING -> {
                }
            }
        })
    }

    private fun removeObservers() {
        viewModel.usdExchangeRateLiveData.removeObservers(this)
        viewModel.eurExchangeRateLiveData.removeObservers(this)
        viewModel.currentBalanceLiveData.removeObservers(this)
        viewModel.transactionsLiveData.removeObservers(this)
        viewModel.incomeLiveData.removeObservers(this)
        viewModel.expenseLiveData.removeObservers(this)
    }

    private fun processErrorState() {
        showToast(R.string.error_loading_data)
    }

    private fun processSuccessFirstExchangeRate(rate: Double?) {
        currency_top_exchange_rate.text = formatter.format(rate)
    }

    private fun processSuccessSecondExchangeRate(rate: Double?) {
        currency_bottom_exchange_rate.text = formatter.format(rate)
    }

    private fun showCurrencyAmount(currencyAmount: CurrencyAmount) {
        tv_balance.setSymbol(
                when (currencyAmount.currency) {
                    Currency.RUB -> getString(R.string.rub_sign)
                    Currency.EUR -> getString(R.string.euro_sign)
                    Currency.USD -> getString(R.string.dollar_sign)
                })
        tv_balance.amount = currencyAmount.amount.toFloat()
    }

    private fun processSuccessTransactionsResponse(data: List<Transaction>) {
        transactionsAdapter.updateDataSet(data)
    }

    private fun showToast(msgId: Int) {
        Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show()
    }
}
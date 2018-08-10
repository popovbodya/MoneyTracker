package ru.popov.bodya.presentation.addtransaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.add_transaction_fragment_layout.*
import ru.popov.bodya.R
import ru.popov.bodya.core.mvwhatever.AppFragment
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.popov.bodya.presentation.addtransaction.adapter.CategoriesAdapter
import timber.log.Timber
import javax.inject.Inject

class AddTransactionFragment : AppFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var viewModel: AddTransactionViewModel

    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var selectedWallet: WalletType
    private lateinit var selectedCurrency: Currency
    private lateinit var selectedCategory: TransactionsCategory
    private lateinit var fieldsWatcherDisposable: Disposable
    private var isIncome = false

    companion object {
        const val INCOME_KEY = "IS_INCOME"
        fun newInstance(isIncome: Boolean) = AddTransactionFragment().apply {
            val args = Bundle()
            args.putBoolean(INCOME_KEY, isIncome)
            arguments = args
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        isIncome = when (arguments?.getBoolean(INCOME_KEY)) {
            true -> true
            false -> false
            null -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parentView = inflater.inflate(R.layout.add_transaction_fragment_layout, container, false)
        setHasOptionsMenu(true)
        initToolbar(parentView)
        return parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModel()
        viewModel.fetchTransactionCategories(isIncome)
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
        fieldsWatcherDisposable.dispose()
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

    private fun subscribeToViewModel() {
        viewModel.transactionCategoriesLiveData.observe(this, Observer { categoriesList ->
            categoriesList?.apply {
                selectedCategory = this[0]
                categoriesAdapter.setCategoriesList(this)
            }
        })
    }

    private fun initToolbar(parentView: View) {
        val toolbar = parentView.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity
        toolbar.title = when (isIncome) {
            true -> getString(R.string.new_income)
            false -> getString(R.string.new_expense)
        }
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initUI() {
        initCategoriesList()
        initInputListeners()
        initCreateTransactionButton()
        initRadioGroups()
    }

    private fun initCategoriesList() {
        categoriesAdapter = CategoriesAdapter(object : OnItemSelectedCallback {
            override fun onCategorySelected(type: TransactionsCategory) {
                selectedCategory = type
                Timber.d("selectedCategory= $selectedCategory")
            }
        })
        categories_recycler_view.adapter = categoriesAdapter
    }

    private fun initInputListeners() {
        val inputObserver: Observable<Boolean> = Observable.combineLatest(
                RxTextView.textChanges(transaction_sum_edit_text),
                RxTextView.textChanges(comment_edit_text),
                BiFunction { amount, comment ->
                    amount.isNotEmpty()
                            && comment.isNotEmpty()
                            && this::selectedCurrency.isInitialized
                            && rg_currencies.checkedRadioButtonId != -1
                            && wallets_segmented_group.checkedRadioButtonId != -1
                })
        fieldsWatcherDisposable = inputObserver.subscribe(btn_create_transaction::setEnabled)

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                periodic_transaction_text_view.text = when (progress) {
                    0 -> resources.getString(R.string.periodic_transaction_no_repetition)
                    1 -> resources.getString(R.string.periodic_transaction_1_hour)
                    2 -> resources.getString(R.string.periodic_transaction_12_hour)
                    3 -> resources.getString(R.string.periodic_transaction_1_day)
                    4 -> resources.getString(R.string.periodic_transaction_2_day)
                    5 -> resources.getString(R.string.periodic_transaction_7_day)
                    6 -> resources.getString(R.string.periodic_transaction_1_month)
                    else -> resources.getString(R.string.periodic_transaction_no_repetition)
                }
                viewModel.onTransactionPeriodChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun initCreateTransactionButton() {
        btn_create_transaction.setOnClickListener {
            val amount = transaction_sum_edit_text.text.toString().toDouble()
            val comment = comment_edit_text.text.toString()
            transaction_sum_edit_text.clearFocus()
            comment_edit_text.clearFocus()
            viewModel.onAddTransactionButtonClick(selectedWallet, selectedCategory, selectedCurrency, amount, comment, periodic_transaction_text_view.text.toString())
        }
    }

    private fun initRadioGroups() {

        rg_currencies.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rub_radio_button -> selectedCurrency = Currency.RUB
                R.id.usd_radio_button -> selectedCurrency = Currency.USD
                R.id.euro_radio_button -> selectedCurrency = Currency.EUR
            }
        }
        wallets_segmented_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.cash_radio_button -> selectedWallet = WalletType.CASH
                R.id.bank_account_radio_button -> selectedWallet = WalletType.BANK_ACCOUNT
                R.id.credit_radio_button -> selectedWallet = WalletType.CREDIT_CARD
            }
        }
    }

    private fun removeObservers() {
        viewModel.transactionCategoriesLiveData.removeObservers(this)
    }

    interface OnItemSelectedCallback {
        fun onCategorySelected(type: TransactionsCategory)
    }
}
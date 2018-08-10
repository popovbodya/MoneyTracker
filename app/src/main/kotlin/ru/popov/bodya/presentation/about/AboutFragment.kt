package ru.popov.bodya.presentation.about

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_about_app.*
import ru.popov.bodya.R
import ru.popov.bodya.core.mvwhatever.AppFragment
import javax.inject.Inject

class AboutFragment : AppFragment() {

    @Inject
    lateinit var viewModel: AboutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parentView: View = inflater.inflate(R.layout.fragment_about_app, container, false)
        initToolbar(parentView)
        return parentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        viewModel.getCurrentAppVersion()
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

    private fun initToolbar(parentView: View) {
        setHasOptionsMenu(true)
        val toolbar = parentView.findViewById<Toolbar>(R.id.toolbar)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.about_application)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViewModel() {
        viewModel.currentVersionLiveData.observe(this, Observer { app_version_text_view.text = resources.getString(R.string.version_code, it) })
    }
}
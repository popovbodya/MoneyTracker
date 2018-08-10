package ru.popov.bodya.presentation.about

import android.arch.lifecycle.MutableLiveData
import ru.popov.bodya.BuildConfig
import ru.popov.bodya.core.mvwhatever.AppViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 *  @author popovbodya
 */
class AboutViewModel @Inject constructor(private val router: Router) : AppViewModel() {

    val currentVersionLiveData = MutableLiveData<String>()

    fun getCurrentAppVersion() = currentVersionLiveData.postValue(BuildConfig.VERSION_NAME)

    fun onHomeButtonClicked() {
        router.exit()
    }
}
package ru.popov.bodya.di.common.app

import ru.popov.bodya.app.HowMoneyApplication

/**
 *  @author popovbodya
 */
object AppInjector {

    fun init(howMoneyApp: HowMoneyApplication) {

        DaggerAppComponent.builder()
                .application(howMoneyApp)
                .appContext(howMoneyApp)
                .build()
                .inject(howMoneyApp)
    }
}
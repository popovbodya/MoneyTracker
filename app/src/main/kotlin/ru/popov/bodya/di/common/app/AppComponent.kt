package ru.popov.bodya.di.common.app

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import ru.popov.bodya.app.MoneyTrackerApplication
import ru.popov.bodya.core.dagger.ApplicationContext
import ru.popov.bodya.di.common.modules.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivitiesBuildersModule::class,
    RxModule::class,
    PersistenceModule::class,
    NetworkModule::class,
    ViewModelModule::class,
    NavigationModule::class
])
interface AppComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun appContext(@ApplicationContext context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(moneyTrackerApplication: MoneyTrackerApplication)
}

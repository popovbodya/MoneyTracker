package ru.popov.bodya.di.common.modules

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import ru.popov.bodya.core.dagger.ApplicationContext
import ru.popov.bodya.data.database.AppDatabase
import ru.popov.bodya.data.database.preferences.SharedPreferencesWrapper
import ru.popov.bodya.data.database.transactions.dao.TransactionsDao
import javax.inject.Singleton

@Module
class PersistenceModule {

    @Singleton
    @Provides
    fun provideTransactionsDao(db: AppDatabase): TransactionsDao = db.transactionsDao

    @Singleton
    @Provides
    fun provideDb(app: Application) =
            Room.databaseBuilder(app, AppDatabase::class.java, "MoneyTracker.db")
                    .fallbackToDestructiveMigration()
                    .build()

    @Provides
    fun provideSharedPreferencesWrapper(sharedPreferences: SharedPreferences): SharedPreferencesWrapper =
            SharedPreferencesWrapper(sharedPreferences)

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
}

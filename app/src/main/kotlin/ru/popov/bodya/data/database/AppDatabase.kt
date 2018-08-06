package ru.popov.bodya.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import ru.popov.bodya.data.database.transactions.converters.entities.CurrencyTypeConverter
import ru.popov.bodya.data.database.transactions.converters.entities.TimeStampConverter
import ru.popov.bodya.data.database.transactions.converters.entities.TransactionTypeConverter
import ru.popov.bodya.data.database.transactions.converters.entities.WalletTypeConverter
import ru.popov.bodya.data.database.transactions.dao.TransactionsDao
import ru.popov.bodya.data.database.transactions.entities.TransactionEntity

@TypeConverters(CurrencyTypeConverter::class, TimeStampConverter::class, TransactionTypeConverter::class, WalletTypeConverter::class)
@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val transactionsDao: TransactionsDao
}

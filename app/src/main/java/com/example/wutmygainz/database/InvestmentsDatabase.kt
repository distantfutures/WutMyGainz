package com.example.wutmygainz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Investments::class], version = 2, exportSchema = false)
abstract class InvestmentsDatabase: RoomDatabase() {
    abstract val investmentDatabaseDao: InvestmentsDatabaseDAO

    companion object {
        @Volatile
        private var INSTANCE: InvestmentsDatabase? = null

        fun getInstance(context: Context): InvestmentsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        InvestmentsDatabase::class.java,
                        "investment_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
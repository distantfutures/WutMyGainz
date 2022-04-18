package com.example.wutmygainz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wutmygainz.network.DataObject

@Database(entities = [Investments::class, DataObject::class], version = 3, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract val investmentDatabaseDao: InvestmentsDatabaseDao
    abstract val dataObjectDao: DataObjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
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
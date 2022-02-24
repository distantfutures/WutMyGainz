package com.example.wutmygainz.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update

@Dao
interface InvestmentsDatabaseDAO {
    @Insert
    fun insertNew(investments: Investments)
    @Update
    fun update(investments: Investments)
    @Query("SELECT * from cryptocurrency_investment_table WHERE investmentId = :key")
    fun get(key: Long): Investments?
    @Query("DELETE FROM cryptocurrency_investment_table")
    fun deleteAll()
    @Query("SELECT * FROM cryptocurrency_investment_table ORDER BY purchase_date DESC")
    fun getAllInvestments(): LiveData<List<Investments>>
}
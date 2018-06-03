package com.tiagobagni.myshoppinglist.stock

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: StockItem)

    @Query("select * from stock_items")
    fun getAll(): LiveData<List<StockItem>>

    @Query("select * from stock_items where name like :search")
    fun get(search: String): LiveData<List<StockItem>>

    @Delete
    fun delete(items: List<StockItem>)
}
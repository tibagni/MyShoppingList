package com.tiagobagni.myshoppinglist.stock

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: StockItem)

    @Query("select * from stock_items order by name")
    fun getAll(): LiveData<List<StockItem>>

    @Query("select * from stock_items where name like :search order by name")
    fun get(search: String): LiveData<List<StockItem>>

    @Delete
    fun delete(items: List<StockItem>)
}
package com.tiagobagni.myshoppinglist.archive

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface ArchivedShoppingListItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<ArchivedShoppingListItem>)

    @Query("select distinct archiveTimestamp from archived_shopping_list_items order by archiveTimestamp desc")
    fun getArchivedTimestamps(): LiveData<List<Long>>

    @Query("select distinct archiveTimestamp from archived_shopping_list_items order by archiveTimestamp desc")
    fun getArchivedTimestampsSync(): List<Long>

    @Query("select * from archived_shopping_list_items where archiveTimestamp = :timestamp")
    fun getArchivedItemsFrom(timestamp: Long): LiveData<List<ArchivedShoppingListItem>>

    @Query("select * from archived_shopping_list_items where stockItemId = :stockItemId ")
    fun getArchivedItemsOf(stockItemId: Int): LiveData<List<ArchivedShoppingListItem>>

    @Query("delete from archived_shopping_list_items where archiveTimestamp = :timestamp")
    fun deleteItemsFrom(timestamp: Long)
}
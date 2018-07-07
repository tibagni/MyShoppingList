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

    @Query("select distinct archiveTimestamp from archived_shopping_list_items")
    fun getArchivedTimestamps(): LiveData<List<Long>>

    @Query("select * from archived_shopping_list_items where archiveTimestamp = :timestamp")
    fun getArchivedItemsFrom(timestamp: Long): LiveData<List<ArchivedShoppingListItem>>
}
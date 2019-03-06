package com.tiagobagni.myshoppinglist.archive

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArchivedShoppingListItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<ArchivedShoppingListItem>)

    @Query("select distinct archiveTimestamp, listName from archived_shopping_list_items order by archiveTimestamp desc")
    fun getArchivedLists(): LiveData<List<ArchivedListInfo>>

    @Query("select distinct archiveTimestamp, listName from archived_shopping_list_items order by archiveTimestamp desc")
    fun getArchivedListsSync(): List<ArchivedListInfo>

    @Query("select * from archived_shopping_list_items where archiveTimestamp = :timestamp")
    fun getArchivedItemsFrom(timestamp: Long): LiveData<List<ArchivedShoppingListItem>>

    @Query("select * from archived_shopping_list_items where stockItemId = :stockItemId ")
    fun getArchivedItemsOf(stockItemId: Int): LiveData<List<ArchivedShoppingListItem>>

    @Query("delete from archived_shopping_list_items where archiveTimestamp = :timestamp")
    fun deleteItemsFrom(timestamp: Long)
}
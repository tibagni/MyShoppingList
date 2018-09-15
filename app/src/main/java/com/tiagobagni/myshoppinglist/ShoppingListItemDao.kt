package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface ShoppingListItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(items: List<ShoppingListItem>)

    @Query("select * from shopping_list_items where listName = :listName order by name")
    fun getAllItemsOrdered(listName: String): LiveData<List<ShoppingListItem>>

    @Query("select * from shopping_list_items where listName = :listName and checked = 1")
    fun getCheckedItems(listName: String): LiveData<List<ShoppingListItem>>

    @Update
    fun updateItem(item: ShoppingListItem)

    @Query("delete from shopping_list_items where listName = :listName")
    fun deleteAll(listName: String)

    @Delete
    fun delete(items: List<ShoppingListItem>)
}
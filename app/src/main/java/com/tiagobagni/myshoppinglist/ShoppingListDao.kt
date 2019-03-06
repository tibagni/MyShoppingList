package com.tiagobagni.myshoppinglist

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(shoppingList: ShoppingList): Long

    @Query("select * from active_shopping_list order by listName")
    fun getAllListsOrdered(): LiveData<List<ShoppingList>>

    @Delete
    fun delete(list: ShoppingList)
}
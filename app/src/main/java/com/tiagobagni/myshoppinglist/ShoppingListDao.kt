package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface ShoppingListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<ShoppingListItem>)

    @Query("select * from shopping_list_items")
    fun getAll(): LiveData<List<ShoppingListItem>>

    @Query("select * from shopping_list_items where checked = 1")
    fun getChecked(): LiveData<List<ShoppingListItem>>

    @Query("select * from shopping_list_items where checked = 0")
    fun getUnChecked(): LiveData<List<ShoppingListItem>>

    @Query("update shopping_list_items set checked = :checked where id = :id")
    fun setChecked(id: Int, checked: Boolean)

    @Query("delete from shopping_list_items")
    fun deleteAll();
}
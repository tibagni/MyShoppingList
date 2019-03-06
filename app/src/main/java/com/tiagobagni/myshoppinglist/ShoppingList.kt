package com.tiagobagni.myshoppinglist

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "active_shopping_list")
data class ShoppingList(@PrimaryKey val listName: String) : Parcelable
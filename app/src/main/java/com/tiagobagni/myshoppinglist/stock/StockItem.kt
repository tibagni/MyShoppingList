package com.tiagobagni.myshoppinglist.stock

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.tiagobagni.myshoppinglist.icons.Icon
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "stock_items")
data class StockItem(val name: String,
                     val icon: Icon = Icon.NONE,
                     @PrimaryKey(autoGenerate = true) var id: Int? = null) : Parcelable
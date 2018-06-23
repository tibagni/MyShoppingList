package com.tiagobagni.myshoppinglist

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.tiagobagni.myshoppinglist.icons.Icon
import com.tiagobagni.myshoppinglist.stock.StockItem
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "shopping_list_items", foreignKeys = [ForeignKey(
        entity = StockItem::class,
        parentColumns = ["id"],
        childColumns = ["stockItemId"],
        onDelete = CASCADE
    )]
)
data class ShoppingListItem(
    val stockItemId: Int,
    val name: String,
    val icon: Icon = Icon.NONE,
    val comment: String = "",
    val checked: Boolean = false,
    val pricePaid: Double = 0.0,
    @PrimaryKey(autoGenerate = true) var id: Int? = null
) : Parcelable
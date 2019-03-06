package com.tiagobagni.myshoppinglist.archive

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.tiagobagni.myshoppinglist.icons.Icon
import com.tiagobagni.myshoppinglist.stock.StockItem
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "archived_shopping_list_items", foreignKeys = [ForeignKey(
        entity = StockItem::class,
        parentColumns = ["id"],
        childColumns = ["stockItemId"],
        onDelete = CASCADE
    )]
)
data class ArchivedShoppingListItem(
    val listName: String,
    val stockItemId: Int,
    val name: String,
    val icon: Icon = Icon.NONE,
    val comment: String = "",
    val pricePaid: Double = 0.0,
    val archiveTimestamp: Long,
    @PrimaryKey(autoGenerate = true) var id: Int? = null
) : Parcelable
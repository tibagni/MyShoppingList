package com.tiagobagni.myshoppinglist

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListItem
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListItemDao
import com.tiagobagni.myshoppinglist.icons.IconTypeConverter
import com.tiagobagni.myshoppinglist.stock.StockDao
import com.tiagobagni.myshoppinglist.stock.StockItem

@Database(
    entities = [
        StockItem::class,
        ShoppingList::class,
        ShoppingListItem::class,
        ArchivedShoppingListItem::class
    ], version = 1
)
@TypeConverters(IconTypeConverter::class)
abstract class MyShoppingListDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingListItemDao(): ShoppingListItemDao
    abstract fun archivedShoppingListDao(): ArchivedShoppingListItemDao
}
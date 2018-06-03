package com.tiagobagni.myshoppinglist.icons

import android.arch.persistence.room.TypeConverter
import com.tiagobagni.myshoppinglist.icons.Icon

class IconTypeConverter {

    @TypeConverter
    fun iconIdToIcon(iconId: Int) : Icon = Icon.values()[iconId + 1]

    @TypeConverter
    fun iconToIconId(icon: Icon) : Int = (icon.ordinal - 1)
}
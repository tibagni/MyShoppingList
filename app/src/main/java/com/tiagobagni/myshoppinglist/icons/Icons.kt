package com.tiagobagni.myshoppinglist.icons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import com.tiagobagni.myshoppinglist.R
import kotlinx.android.synthetic.main.icon_item.view.*

internal const val INVALID_POSITION = -1

enum class Icon(val resId: Int) {
    NONE(INVALID_POSITION),
    GROCERY(R.drawable.ic_grocery_store),
    BEACH(R.drawable.ic_beach),
    CAR(R.drawable.ic_car),
    COFFEE(R.drawable.ic_coffee),
    ELECTRONIC(R.drawable.ic_electronic),
    FITNESS(R.drawable.ic_fitness),
    HOME(R.drawable.ic_home),
    LAUNDRY(R.drawable.ic_laundry),
    OFFICE(R.drawable.ic_office),
    STORE(R.drawable.ic_store),
    TOOL(R.drawable.ic_tool)
}

class IconsController(iconsGrid: GridView) {
    private val adapter = IconsAdapter(iconsGrid.context)

    var selectedIcon: Icon
        get() {
            return Icon.values()[adapter.selectedPosition + 1]
        }
        set(value) {
            adapter.selectedPosition = value.ordinal - 1
            adapter.notifyDataSetChanged()
        }

    init {
        iconsGrid.adapter = adapter
        iconsGrid.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    adapter.selectedPosition =
                            if (adapter.selectedPosition == position) INVALID_POSITION else position
                    adapter.notifyDataSetChanged()
                }
    }

}

private class IconsAdapter(context: Context) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private val icons = Icon.values().filter { it != Icon.NONE }
    var selectedPosition = INVALID_POSITION


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.icon_item, parent, false)
        view.iconImage.setImageResource(icons[position].resId)
        view.selectedBackground.visibility =
                if (selectedPosition == position) View.VISIBLE else View.GONE

        return view
    }

    override fun getItem(position: Int) = icons[position]

    override fun getItemId(position: Int) = icons[position].ordinal.toLong()

    override fun getCount() = icons.size
}
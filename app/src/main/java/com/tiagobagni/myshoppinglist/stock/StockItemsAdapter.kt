package com.tiagobagni.myshoppinglist.stock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.adapter.RecyclerListAdapter
import com.tiagobagni.myshoppinglist.extensions.ClickListener
import com.tiagobagni.myshoppinglist.icons.Icon
import com.tiagobagni.selectionmode.SelectionModeHelper
import com.tiagobagni.selectionmode.SelectionModeVHClickListener
import com.tiagobagni.selectionmode.SelectionModeViewHolder
import kotlinx.android.synthetic.main.stock_item.view.*

class StockItemsAdapter(
    private val listener: ClickListener<StockItem>,
    private val selectionModeHelper: SelectionModeHelper
) :
    RecyclerListAdapter<StockItem, StockItemsAdapter.ViewHolder>(),
    SelectionModeVHClickListener {

    init {
        selectionModeHelper.onSelectionCleared = { clearItems(it) }
    }

    class ViewHolder(
        view: View,
        selectionModeHelper: SelectionModeHelper,
        clickListener: SelectionModeVHClickListener
    ) :
        SelectionModeViewHolder<StockItem>(view, selectionModeHelper, clickListener) {
        override fun onBind(item: StockItem) {
            with(itemView) {
                itemName.text = item.name
                if (item.icon != Icon.NONE) {
                    itemIcon.setImageResource(item.icon.resId)
                }
            }
        }
    }

    override fun areItemsTheSame(oldItem: StockItem, newItem: StockItem) =
        oldItem == newItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stock_item, parent, false)
        return ViewHolder(view, selectionModeHelper, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list[position])

    override fun onClick(position: Int, view: View) {
        listener(list[position])
    }

    private fun clearItems(rangesCleared: Set<Pair<Int, Int>>) {
        for ((from, to) in rangesCleared) {
            if (from == to) {
                notifyItemChanged(from)
            } else {
                val count = (to - from) + 1
                notifyItemRangeChanged(from, count)
            }
        }
    }

    fun getItems(itemIndices: Collection<Int>): List<StockItem> {
        return list.filterIndexed { index, _ -> itemIndices.contains(index) }
    }
}
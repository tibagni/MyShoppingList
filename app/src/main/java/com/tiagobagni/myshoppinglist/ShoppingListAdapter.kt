package com.tiagobagni.myshoppinglist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiagobagni.myshoppinglist.adapter.AdapterItem
import com.tiagobagni.myshoppinglist.adapter.RecyclerListAdapter
import com.tiagobagni.myshoppinglist.extensions.AdapterClickListener
import com.tiagobagni.myshoppinglist.extensions.ClickListener
import com.tiagobagni.myshoppinglist.extensions.toCurrencyFormat
import com.tiagobagni.myshoppinglist.icons.Icon
import com.tiagobagni.myshoppinglist.stock.StockItem
import com.tiagobagni.selectionmode.SelectionModeHelper
import com.tiagobagni.selectionmode.SelectionModeVHClickListener
import com.tiagobagni.selectionmode.SelectionModeViewHolder
import kotlinx.android.synthetic.main.list_header.view.*
import kotlinx.android.synthetic.main.shopping_list_item.view.*

class ShoppingListAdapter(
    private val listener: ClickListener<ShoppingListItem>,
    private val selectionModeHelper: SelectionModeHelper
) : RecyclerListAdapter<AdapterItem<ShoppingListItem>, ShoppingListAdapter.ViewHolder>(),
    SelectionModeVHClickListener {
    private val headerItem = AdapterItem<ShoppingListItem>(null, TYPE_HEADER)

    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_HEADER = 1
    }

    init {
        selectionModeHelper.onSelectionCleared = { clearItems(it) }
    }

    class ViewHolder(
        private val view: View,
        selectionModeHelper: SelectionModeHelper,
        clickListener: SelectionModeVHClickListener
    ) : SelectionModeViewHolder<ShoppingListItem>(
        view,
        selectionModeHelper,
        clickListener
    ) {
        override fun onBind(item: ShoppingListItem) {
            with(view) {
                productName.text = item.name
                comments.text = item.comment

                if (item.checked) {
                    checkBox.setImageResource(R.drawable.ic_check_checked)
                    pricePaid.visibility = View.VISIBLE
                    pricePaid.text = item.pricePaid.toCurrencyFormat()
                } else {
                    checkBox.setImageResource(R.drawable.ic_check_unchecked)
                    pricePaid.visibility = View.GONE
                }

                if (item.icon != Icon.NONE) {
                    itemIcon.setImageResource(item.icon.resId)
                }

                comments.visibility = if (item.comment.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        fun bindHeader(headerRes: Int) {
            view.header.setText(headerRes)
        }
    }

    override fun areItemsTheSame(
        oldItem: AdapterItem<ShoppingListItem>,
        newItem: AdapterItem<ShoppingListItem>
    ) =
        oldItem.value?.id == newItem.value?.id

    override fun areContentsTheSame(
        oldItem: AdapterItem<ShoppingListItem>,
        newItem: AdapterItem<ShoppingListItem>
    ) =
        oldItem.value == newItem.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = if (viewType == TYPE_HEADER) {
            inflater.inflate(R.layout.list_header, parent, false)
        } else {
            inflater.inflate(R.layout.shopping_list_item, parent, false)
        }
        return ViewHolder(view, selectionModeHelper, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            holder.bindHeader(R.string.completed_header)
        } else {
            holder.bind(list[position].value!!)
        }
    }

    override fun onClick(position: Int, view: View) {
        val item = list[position].value
        item?.let { listener(it) }
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

    override fun onPrepareNewData(newData: List<AdapterItem<ShoppingListItem>>):
            List<AdapterItem<ShoppingListItem>> {
        val uncheckedItems = newData.filter { it.value?.checked == false }
        val checkedItems = newData.filter { it.value?.checked == true }

        if (checkedItems.isEmpty()) {
            return newData
        }

        return uncheckedItems + headerItem + checkedItems
    }

    override fun getItemViewType(position: Int) = list[position].viewType

    fun getItems(itemIndices: Collection<Int>): List<ShoppingListItem> {
        return list.filterIndexed { index, _ -> itemIndices.contains(index) }
            .mapNotNull { it.value }
    }

    fun selectAll() {
        selectionModeHelper.markAsSelected(list.mapIndexed { index, _ -> index })
        // Here we need to update the state of all views, so, it is fine to call
        // notifyDataSetChanged
        notifyDataSetChanged()
    }
}

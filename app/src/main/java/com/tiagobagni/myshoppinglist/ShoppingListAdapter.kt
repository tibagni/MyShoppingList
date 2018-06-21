package com.tiagobagni.myshoppinglist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiagobagni.myshoppinglist.adapter.AdapterItem
import com.tiagobagni.myshoppinglist.adapter.RecyclerListAdapter
import com.tiagobagni.myshoppinglist.extensions.AdapterClickListener
import com.tiagobagni.myshoppinglist.extensions.ClickListener
import com.tiagobagni.myshoppinglist.icons.Icon
import kotlinx.android.synthetic.main.list_header.view.*
import kotlinx.android.synthetic.main.shopping_list_item.view.*

class ShoppingListAdapter(
    private val clickListener: ClickListener<ShoppingListItem>,
    private val longClickListener: ClickListener<ShoppingListItem>
) : RecyclerListAdapter<AdapterItem<ShoppingListItem>, ShoppingListAdapter.ViewHolder>(),
    AdapterClickListener {
    private val headerItem = AdapterItem<ShoppingListItem>(null, TYPE_HEADER)

    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_HEADER = 1
    }

    class ViewHolder(
        private val view: View,
        private val clickListener: AdapterClickListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        fun bind(item: ShoppingListItem) {
            with(view) {
                productName.text = item.name
                comments.text = item.comment

                val checkedRes = if (item.checked) {
                    R.drawable.ic_check_checked
                } else {
                    R.drawable.ic_check_unchecked
                }
                checkBox.setImageResource(checkedRes)

                setOnClickListener(this@ViewHolder)
                setOnLongClickListener(this@ViewHolder)
                if (item.icon != Icon.NONE) {
                    itemIcon.setImageResource(item.icon.resId)
                }

                comments.visibility = if (item.comment.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        fun bindHeader(headerRes: Int) {
            view.header.setText(headerRes)
        }

        override fun onClick(view: View?) {
            clickListener.onItemClicked(layoutPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            clickListener.onItemLongClicked(layoutPosition)
            return true
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
        return ViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            holder.bindHeader(R.string.completed_header)
        } else {
            holder.bind(list[position].value!!)
        }
    }

    override fun onItemClicked(itemPosition: Int) {
        val item = list[itemPosition].value
        item?.let { clickListener(it) }
    }

    override fun onItemLongClicked(itemPosition: Int) {
        val item = list[itemPosition].value
        item?.let { longClickListener(it) }
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
}

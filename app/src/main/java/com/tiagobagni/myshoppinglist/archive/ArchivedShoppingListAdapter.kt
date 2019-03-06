package com.tiagobagni.myshoppinglist.archive

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.adapter.RecyclerListAdapter
import com.tiagobagni.myshoppinglist.extensions.toCurrencyFormat
import kotlinx.android.synthetic.main.shopping_list_item.view.*

class ArchivedShoppingListAdapter() :
    RecyclerListAdapter<ArchivedShoppingListItem, ArchivedShoppingListAdapter.ViewHolder>() {

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ArchivedShoppingListItem) {
            with(view) {
                productName.text = item.name
                comments.text = item.comment
                checkBox.setImageResource(R.drawable.ic_check_checked)
                pricePaid.visibility = View.VISIBLE
                pricePaid.text = item.pricePaid.toCurrencyFormat()

                comments.visibility = if (item.comment.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun areItemsTheSame(
        oldItem: ArchivedShoppingListItem,
        newItem: ArchivedShoppingListItem
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ArchivedShoppingListItem,
        newItem: ArchivedShoppingListItem
    ) = oldItem == newItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.shopping_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

package com.tiagobagni.myshoppinglist.stock

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.adapter.RecyclerListAdapter
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListItem
import com.tiagobagni.myshoppinglist.extensions.toCurrencyFormat
import com.tiagobagni.myshoppinglist.extensions.toFormattedDate
import kotlinx.android.synthetic.main.stock_item_details_item.view.*

class StockItemsDetailsAdapter :
    RecyclerListAdapter<ArchivedShoppingListItem, StockItemsDetailsAdapter.ViewHolder>() {

    var mostExpensivePrice = 0.0
    var cheapestPrice = 0.0

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private var originalColor: Int? = null

        fun bind(item: ArchivedShoppingListItem) {
            with(view) {
                timestamp.text = item.archiveTimestamp.toFormattedDate()
                comments.text = item.comment
                pricePaid.text = item.pricePaid.toCurrencyFormat()
                originalColor?.let { pricePaid.setTextColor(it) }

                comments.visibility = if (item.comment.isEmpty()) View.GONE else View.VISIBLE

            }
        }

        fun setPriceColor(color: Int) {
            originalColor = view.pricePaid.currentTextColor
            view.pricePaid.setTextColor(color)
        }
    }

    override fun areItemsTheSame(
        oldItem: ArchivedShoppingListItem,
        newItem: ArchivedShoppingListItem
    ): Boolean {
        if (oldItem.id != newItem.id) {
            return false
        }

        return oldItem.pricePaid != mostExpensivePrice && oldItem.pricePaid != cheapestPrice
    }

    override fun areContentsTheSame(
        oldItem: ArchivedShoppingListItem,
        newItem: ArchivedShoppingListItem
    ) = oldItem == newItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stock_item_details_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        val isMostExpensive = mostExpensivePrice == item.pricePaid
        val isCheapest = cheapestPrice == item.pricePaid

        if (isMostExpensive != isCheapest) {
            val color = if (isMostExpensive) Color.RED else Color.BLUE
            holder.setPriceColor(color)
        }
    }
}

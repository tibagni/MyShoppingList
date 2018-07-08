package com.tiagobagni.myshoppinglist.stock

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListItem
import com.tiagobagni.myshoppinglist.extensions.toCurrencyFormat
import kotlinx.android.synthetic.main.stock_item_details.view.*
import org.jetbrains.anko.bundleOf
import org.koin.android.architecture.ext.viewModel

class StockItemDetailsDialog: DialogFragment() {
    private lateinit var itemName: String
    private var itemId: Int = 0

    private val adapter = StockItemsDetailsAdapter()
    private val viewModel by viewModel<StockItemsDetailsViewModel>()

    companion object {
        private const val ITEM_ID_ARG = "id"
        private const val ITEM_NAME_ARG = "name"

        fun newInstance(itemId: Int, itemName: String): StockItemDetailsDialog {
            val dialog = StockItemDetailsDialog()
            dialog.arguments = bundleOf(
                ITEM_ID_ARG to itemId,
                ITEM_NAME_ARG to itemName)

            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments ?: throw IllegalArgumentException("No arguments provided!")
        itemName = args[ITEM_NAME_ARG] as String
        itemId = args[ITEM_ID_ARG] as Int
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.stock_item_details, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            title.text = itemName
            historyItems.layoutManager = LinearLayoutManager(context)
            historyItems.adapter = adapter
            okButton.setOnClickListener { dismiss() }
        }

        viewModel.getArchivedItemsOf(itemId).observe(this, Observer {
            updateView(it ?: emptyList())
            dialog.window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        })
    }

    private fun updateView(allArchivedItems: List<ArchivedShoppingListItem>) {
        val prices = allArchivedItems.map { it.pricePaid }
        val avgPrice = prices.average()
        val mostExp = prices.max() ?: 0.0
        val cheapest = prices.min() ?: 0.0

        view?.avgVal?.text = avgPrice.toCurrencyFormat()
        adapter.cheapestPrice = cheapest
        adapter.mostExpensivePrice = mostExp

        adapter.updateData(allArchivedItems)
    }
}
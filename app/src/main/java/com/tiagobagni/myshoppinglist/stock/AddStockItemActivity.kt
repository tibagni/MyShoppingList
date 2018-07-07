package com.tiagobagni.myshoppinglist.stock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tiagobagni.myshoppinglist.FabProvider
import com.tiagobagni.myshoppinglist.R

import kotlinx.android.synthetic.main.activity_add_stock_item.*

class AddStockItemActivity : AppCompatActivity(), FabProvider {
    companion object {
        const val STOCK_ITEMS_RESULT = "StockItemsResult"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stock_item)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun configureFab(imageRes: Int?, clickListener: () -> Unit) {
        fab.show()
        fab.setOnClickListener { clickListener() }
        imageRes?.let { fab.setImageResource(imageRes) }
    }

    override fun hideFab() {
        fab.hide()
    }

    fun finishWithResult(item: StockItem) {
        finishWithResult(arrayOf(item))
    }

    fun finishWithResult(items: Array<StockItem>) {
        setResult(
            Activity.RESULT_OK,
            Intent().apply { putExtra(STOCK_ITEMS_RESULT, items) })
        finish()
    }
}

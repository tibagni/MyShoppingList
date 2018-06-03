package com.tiagobagni.myshoppinglist.stock

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.SimpleAdapter
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.icons.Icon
import com.tiagobagni.myshoppinglist.icons.IconsController
import kotlinx.android.synthetic.main.create_stock_item.view.*

class NewStockDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.create_stock_item, null)
        val iconsGrid = view.iconsGrid
        val controller = IconsController(iconsGrid)

        val dialog =  AlertDialog.Builder(context!!)
            .setTitle(R.string.title_new_stock_item)
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null) // Only dismiss it in cancel
            .create()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // The default behavior of positive button is to always dismiss the dialog.
        // Since we need to perform some validation and only dismiss in case everything is
        // fine, we override the button click behavior here to prevent the dialog from getting
        // dismissed when validation is not successful
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = view.itemName.text.toString()
                if (name.isEmpty()) {
                    view.itemName.error = getString(R.string.error_empty_name)
                    return@setOnClickListener
                }

                dismiss()
                val callback = parentFragment as? Callback ?: return@setOnClickListener
                val icon = controller.selectedIcon

                callback.onNewStockItemCreated(StockItem(name, icon))
            }
        }

        return dialog
    }

    interface Callback {
        fun onNewStockItemCreated(newItem: StockItem)
    }
}
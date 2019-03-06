package com.tiagobagni.myshoppinglist.stock

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import com.tiagobagni.myshoppinglist.R
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
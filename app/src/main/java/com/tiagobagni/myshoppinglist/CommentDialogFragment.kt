package com.tiagobagni.myshoppinglist


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.input_dialog.view.*


class CommentDialogFragment : DialogFragment() {

    companion object {
        private const val ITEM_ARG = "item_arg"

        fun newInstance(item: ShoppingListItem): CommentDialogFragment {
            val dialog = CommentDialogFragment()
            val args = Bundle()
            args.putParcelable(ITEM_ARG, item)
            dialog.arguments = args

            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.input_dialog, null)

        val item = arguments?.get(ITEM_ARG) as? ShoppingListItem
                ?: throw IllegalArgumentException("No item provided!")

        view.inputText.setText(item.comment, TextView.BufferType.EDITABLE)
        view.inputDescription.text = getString(R.string.shopping_list_item_comment_description)
        val dialog = AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.comment_title))
            .setView(view)
            .setPositiveButton(android.R.string.ok, { _, _ ->
                dismiss()
                val callback = parentFragment as? Callback ?: return@setPositiveButton
                callback.onCommentEdited(item, view.inputText.text.toString())
            })
            .setNegativeButton(android.R.string.cancel, null) // Only dismiss it in cancel
            .create()

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return dialog
    }

    interface Callback {
        fun onCommentEdited(item: ShoppingListItem, comment: String)
    }
}
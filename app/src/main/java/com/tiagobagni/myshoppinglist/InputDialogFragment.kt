package com.tiagobagni.myshoppinglist


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.input_dialog.view.*


class InputDialogFragment : DialogFragment() {

    companion object {
        private const val ID_ARG = "id"
        private const val ITEM_ARG = "item"
        private const val TITLE_ARG = "title"
        private const val DESCRIPTION_ARG = "description"
        private const val DEFAULT_INPUT_ARG = "default_input"
        private const val INPUT_TYPE_ARG = "input_type"

        fun newInstance(
            dialogId: Int = 0,
            item: ShoppingListItem,
            title: String = "",
            description: String = "",
            defaultInput: String = "",
            inputType: Int = EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS
        ): InputDialogFragment {
            val dialog = InputDialogFragment()
            val args = Bundle()
            args.putInt(ID_ARG, dialogId)
            args.putParcelable(ITEM_ARG, item)
            args.putString(TITLE_ARG, title)
            args.putString(DESCRIPTION_ARG, description)
            args.putString(DEFAULT_INPUT_ARG, defaultInput)
            args.putInt(INPUT_TYPE_ARG, inputType)
            dialog.arguments = args

            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.input_dialog, null)

        val args = arguments ?: throw IllegalArgumentException("No arguments provided!")
        val dialogId = args.getInt(ID_ARG)
        val item = args.get(ITEM_ARG) as? ShoppingListItem
                ?: throw IllegalArgumentException("No item provided!")

        val defInput = args.getString(DEFAULT_INPUT_ARG)
        val title = args.getString(TITLE_ARG)
        with(view) {
            inputText.setText(defInput, TextView.BufferType.EDITABLE)
            inputText.inputType = args.getInt(INPUT_TYPE_ARG)
            val description = args.getString(DESCRIPTION_ARG, "")
            if (TextUtils.isEmpty(description)) {
                inputDescription.visibility = View.GONE
            } else {
                inputDescription.text = description
            }
        }

        val dialog = AlertDialog.Builder(context!!)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(android.R.string.ok, { _, _ ->
                dismiss()
                val callback = parentFragment as? Callback ?: return@setPositiveButton
                callback.onInputDialogConfirmed(dialogId, item, view.inputText.text.toString())
            })
            .setNegativeButton(android.R.string.cancel, null) // Only dismiss it in cancel
            .create()

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return dialog
    }

    interface Callback {
        fun onInputDialogConfirmed(dialogId: Int, item: ShoppingListItem, input: String)
    }
}
package com.tiagobagni.myshoppinglist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.jetbrains.anko.bundleOf
import java.lang.IllegalArgumentException

class ConfirmationDialogFragment : DialogFragment() {

    companion object {
        const val ID_ARG = "id"
        const val TITLE_ARG = "title"
        const val MESSAGE_ARG = "message"

        fun newInstance(id: Int, title: String, message: String) : ConfirmationDialogFragment {
            val dialog = ConfirmationDialogFragment()
            dialog.arguments = bundleOf(
                ID_ARG to id,
                TITLE_ARG to title,
                MESSAGE_ARG to message)

            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments ?: throw IllegalArgumentException("No arguments provided")

        return AlertDialog.Builder(context!!)
            .setTitle(args[TITLE_ARG] as String)
            .setMessage(args[MESSAGE_ARG] as String)
            .setPositiveButton(android.R.string.ok, { _, _ -> onPositiveButton() })
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    private fun onPositiveButton() {
        val args = arguments ?: throw IllegalArgumentException("No arguments provided")

        val callback = parentFragment as Callback?
        callback?.onConfirmed(args[ID_ARG] as Int)
    }

    interface Callback {
        fun onConfirmed(dialogId: Int)
    }
}
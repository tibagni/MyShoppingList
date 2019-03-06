package com.tiagobagni.myshoppinglist

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.EditorInfo
import com.tiagobagni.myshoppinglist.adapter.AdapterItem
import com.tiagobagni.myshoppinglist.extensions.*
import com.tiagobagni.myshoppinglist.stock.AddStockItemActivity
import com.tiagobagni.myshoppinglist.stock.StockItem
import kotlinx.android.synthetic.main.fragment_shopping_list.*
import org.koin.android.architecture.ext.viewModel
import android.widget.TextView
import com.tiagobagni.selectionmode.ChoiceModeHelperCallback
import com.tiagobagni.selectionmode.SelectionModeHelper


class ShoppingListFragment : Fragment(), InputDialogFragment.Callback,
    ConfirmationDialogFragment.Callback {

    companion object {
        const val ARG_LIST_NAME = "list_name"

        private const val ADD_STOCK_ITEM_REQUEST_CODE = 1

        private const val COMMENT_DIALOG = 0
        private const val PRICE_DIALOG = 1

        private const val ARCHIVE_CONFIRM_DIALOG = 0
        private const val DELETE_LIST_CONFIRM_DIALOG = 1
        private const val DELETE_SELECTED_CONFIRM_DIALOG = 2
    }

    private var actionMode: ActionMode? = null
    private val selectionModeHelper = SelectionModeHelper(object : ChoiceModeHelperCallback {
        override fun onSelectionChanged() {
            updateSelectionCount()
            updateMenu()
        }

        override fun onEnterSelectionMode() {
            startSelectionMode()
        }

        override fun onExitSelectionMode() {
            actionMode?.finish()
        }
    })

    private val shoppingListAdapter = ShoppingListAdapter(
        this::onItemClicked,
        selectionModeHelper
    )
    private val viewModel by viewModel<ShoppingListViewModel>(
        parameters = { mapOf("listName" to listName) }
    )
    private val mainActivity by lazy { activity as MainActivity }
    private val listName by lazy { arguments?.get(ARG_LIST_NAME) as String }

    private var snackbar: Snackbar? = null
    private var menu: Menu? = null

    private var updateMenu = false

    init {
        setHasOptionsMenu(true)
    }

    private fun updateSelectionCount() {
        actionMode?.title = selectionModeHelper.selectedCount.toString()
    }

    private fun updateMenu() {
        val canSelectAll = selectionModeHelper.selectedCount != shoppingListAdapter.itemCount
        val canComment = selectionModeHelper.selectedCount == 1
        actionMode?.menu?.findItem(R.id.action_select_all)?.isVisible = canSelectAll
        actionMode?.menu?.findItem(R.id.action_comment)?.isVisible = canComment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_shopping_list, null)

    override fun onPrepareOptionsMenu(menu: Menu) {
        // This can run after the LiveData comes with the updated value
        // So, to make sure we always initialize the menu correctly,
        // read the showArchiveOption here too so we don't override
        // the correct state with the default state
        menu.findItem(R.id.action_archive)?.isVisible =
                viewModel.showArchiveOption.value ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shopping_list, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                showConfirmDeleteListDialog()
                true
            }
            R.id.action_archive -> {
                showConfirmArchiveDialog()
                true
            }
            else -> false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        selectionModeHelper.saveState(outState)
    }

    private fun startSelectionMode() {
        actionMode = activity?.startActionMode(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.action_comment -> {
                        onCommentSelection()
                        return true
                    }
                    R.id.action_delete -> {
                        showConfirmDeleteSelectedDialog()
                        return true
                    }
                    R.id.action_select_all -> {
                        shoppingListAdapter.selectAll()
                        return true
                    }
                }
                return false
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                mode.menuInflater.inflate(R.menu.menu_context_shopping_list, menu)
                return true
            }
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = true
            override fun onDestroyActionMode(mode: ActionMode?) {
                selectionModeHelper.cancelSelectionMode()
            }
        })
    }

    private fun showConfirmDeleteListDialog() {
        val dialog = ConfirmationDialogFragment.newInstance(
            DELETE_LIST_CONFIRM_DIALOG,
            getString(R.string.delete_title),
            getString(R.string.delete_list_msg)
        )

        dialog.show(childFragmentManager, "confirmDelete")
    }

    private fun showConfirmArchiveDialog() {
        val dialog = ConfirmationDialogFragment.newInstance(
            ARCHIVE_CONFIRM_DIALOG,
            getString(R.string.archive_title),
            getString(R.string.archive_msg)
        )

        dialog.show(childFragmentManager, "confirmArchive")
    }


    private fun showConfirmDeleteSelectedDialog() {
        val dialog = ConfirmationDialogFragment.newInstance(
            DELETE_SELECTED_CONFIRM_DIALOG,
            getString(R.string.delete_title),
            getString(R.string.delete_items_msg)
        )

        dialog.show(childFragmentManager, "confirmDeleteSelected")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.shoppingList.observe(this,
            Observer {
                val list = it?.map { AdapterItem(it, ShoppingListAdapter.TYPE_ITEM) } ?: emptyList()
                shoppingListAdapter.updateData(list)
                if (updateMenu) {
                    updateMenu = false
                    updateMenu()
                }
            })

        viewModel.checkedItems.observe(this, Observer {
            val numOfCheckedItems = it?.size ?: 0
            if (numOfCheckedItems > 0) {
                val totalSpent =
                    it?.map { item -> item.pricePaid }?.reduce { acc, d -> acc + d } ?: 0.0
                showTotalSpent(totalSpent)
            } else {
                hideTotalSpent()
            }
        })

        viewModel.showArchiveOption.observe(this, Observer {
            if (it == true) {
                showArchiveOption()
            } else {
                hideArchiveOption()
            }
        })

        shoppingList.layoutManager = LinearLayoutManager(context)
        shoppingList.emptyView = emptyView
        shoppingList.adapter = shoppingListAdapter

        mainActivity.configureFab(R.drawable.ic_add_shopping_cart) {
            startActivityForResult<AddStockItemActivity>(ADD_STOCK_ITEM_REQUEST_CODE)
        }
        mainActivity.title = listName

        savedInstanceState?.let {
            selectionModeHelper.restoreFromState(it)
            if (selectionModeHelper.isInSelectionMode()) {
                startSelectionMode()
                updateSelectionCount()
                updateMenu = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide the snackbar here as we are being destroyed. Probably
        // another fragment will be shown
        hideTotalSpent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_STOCK_ITEM_REQUEST_CODE -> {
                val items = extractStockItemsList(data)
                val shoppingListItems =
                    items.map { ShoppingListItem(listName, it.id!!, it.name, it.icon) }
                viewModel.addItems(shoppingListItems)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun extractStockItemsList(data: Intent?): List<StockItem> {
        val parcelableArray = data?.getParcelableArrayExtra(AddStockItemActivity.STOCK_ITEMS_RESULT)
        return parcelableArray?.filterIsInstance(StockItem::class.java) ?: listOf()
    }

    private fun onItemClicked(item: ShoppingListItem) {
        if (item.checked) {
            viewModel.updateItem(item) { toggleItemChecked() }
        } else {
            val priceDialog = InputDialogFragment.newInstance(
                dialogId = PRICE_DIALOG,
                item = item,
                title = getString(R.string.how_much_title),
                description = getString(R.string.how_much_description),
                inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL,
                iconRes = R.drawable.ic_money
            )
            priceDialog.show(childFragmentManager, "price")
        }
    }

    private fun onCommentSelection() {
        // This option is only available when one single item is selected
        val item = shoppingListAdapter.getItems(selectionModeHelper.selectedItems).first()
        val commentDialog = InputDialogFragment.newInstance(
            dialogId = COMMENT_DIALOG,
            item = item,
            title = getString(R.string.comment_title),
            description = getString(R.string.shopping_list_item_comment_description),
            defaultInput = item.comment
        )
        commentDialog.show(childFragmentManager, "comment")
    }

    private fun onDeleteSelection() {
        val items = shoppingListAdapter.getItems(selectionModeHelper.selectedItems)
        viewModel.deleteItems(items)
        actionMode?.finish()
    }

    private fun showTotalSpent(totalSpent: Double) {
        val message = getString(R.string.total_spent, totalSpent.toCurrencyFormat())
        snackbar = shoppingList.showCustomSnack(message, Snackbar.LENGTH_INDEFINITE) {
            setBackgroundColor(getColor(R.color.background_material_light))
            val snackBarText = findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            snackBarText.setTextColor(getColor(R.color.secondary_text_default_material_light))
        }
    }

    private fun hideTotalSpent() {
        snackbar?.dismiss()
    }

    private fun showArchiveOption() {
        menu?.findItem(R.id.action_archive)?.isVisible = true
    }

    private fun hideArchiveOption() {
        menu?.findItem(R.id.action_archive)?.isVisible = false
    }

    override fun onInputDialogConfirmed(dialogId: Int, item: ShoppingListItem, input: String) {
        when (dialogId) {
            COMMENT_DIALOG -> viewModel.updateItem(item) { setComment(input) }
            PRICE_DIALOG -> {
                viewModel.updateItem(item) {
                    toggleItemChecked()
                    setPricePaid(input.toDoubleOrZero())
                }
            }
        }
    }

    override fun onConfirmed(dialogId: Int) {
        when (dialogId) {
            DELETE_LIST_CONFIRM_DIALOG -> viewModel.deleteList()
            ARCHIVE_CONFIRM_DIALOG -> viewModel.archiveList()
            DELETE_SELECTED_CONFIRM_DIALOG -> onDeleteSelection()
        }
    }
}
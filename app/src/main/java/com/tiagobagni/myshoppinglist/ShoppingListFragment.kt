package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.EditorInfo
import com.tiagobagni.myshoppinglist.adapter.AdapterItem
import com.tiagobagni.myshoppinglist.extensions.*
import com.tiagobagni.myshoppinglist.stock.AddStockItemActivity
import com.tiagobagni.myshoppinglist.stock.StockItem
import kotlinx.android.synthetic.main.fragment_shopping_list.*
import org.koin.android.architecture.ext.viewModel
import android.widget.TextView


class ShoppingListFragment : Fragment(), InputDialogFragment.Callback {

    private companion object {
        const val ADD_STOCK_ITEM_REQUEST_CODE = 1

        private const val COMMENT_DIALOG = 0
        private const val PRICE_DIALOG = 1
    }

    private val shoppingListAdapter = ShoppingListAdapter(
        this::onItemClicked,
        this::onItemLongClicked
    )
    private val viewModel by viewModel<ShoppingListViewModel>()
    private val mainActivity by lazy { activity as MainActivity }

    private var snackbar: Snackbar? = null
    private var menu: Menu? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_shopping_list, null)

    override fun onPrepareOptionsMenu(menu: Menu?) {
        // This can run after the LiveData comes with the updated value
        // So, to make sure we always initialize the menu correctly,
        // read the listCompletedStatus here too so we don't override
        // the correct state with the default state
        menu?.findItem(R.id.action_archive)?.isVisible =
                viewModel.listCompletedStatus.value ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shopping_list, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_clear -> {
                viewModel.clear()
                true
            }
            R.id.action_archive -> {
                viewModel.archiveList()
                true
            }
            else -> false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.shoppingList.observe(this,
            Observer {
                val list = it?.map { AdapterItem(it, ShoppingListAdapter.TYPE_ITEM) } ?: emptyList()
                shoppingListAdapter.updateData(list)
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

        viewModel.listCompletedStatus.observe(this, Observer {
            if (it == true) {
                showArchiveOrClearOptions()
            } else {
                hideArchiveOrClearOptions()
            }
        })

        shoppingList.layoutManager = LinearLayoutManager(context)
        shoppingList.emptyView = emptyView
        shoppingList.adapter = shoppingListAdapter

        mainActivity.configureFab(R.drawable.ic_add_shopping_cart) {
            startActivityForResult<AddStockItemActivity>(ADD_STOCK_ITEM_REQUEST_CODE)
        }
        mainActivity.title = getString(R.string.title_shopping_list)
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
                val shoppingListItems = items.map { ShoppingListItem(it.id!!, it.name, it.icon) }
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
                inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
            )
            priceDialog.show(childFragmentManager, "price")
        }
    }

    private fun onItemLongClicked(item: ShoppingListItem) {
        val commentDialog = InputDialogFragment.newInstance(
            dialogId = COMMENT_DIALOG,
            item = item,
            title = getString(R.string.comment_title),
            description = getString(R.string.shopping_list_item_comment_description),
            defaultInput = item.comment
        )
        commentDialog.show(childFragmentManager, "comment")
    }

    private fun showTotalSpent(totalSpent: Double) {
        val message = getString(R.string.total_spent, totalSpent.toCurrencyFormat())
        snackbar = shoppingList.showCustomSnack(message, Snackbar.LENGTH_INDEFINITE) {
            setBackgroundColor(getColor(R.color.background_material_light))
            val snackBarText = findViewById<TextView>(android.support.design.R.id.snackbar_text)
            snackBarText.setTextColor(getColor(R.color.secondary_text_default_material_light))
        }
    }

    private fun hideTotalSpent() {
        snackbar?.dismiss()
    }

    private fun showArchiveOrClearOptions() {
        menu?.findItem(R.id.action_archive)?.isVisible = true
    }

    private fun hideArchiveOrClearOptions() {
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
}
package com.tiagobagni.myshoppinglist.stock

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.tiagobagni.myshoppinglist.FabProvider
import com.tiagobagni.myshoppinglist.MainActivity
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.selectionmode.SelectionModeHelper
import com.tiagobagni.selectionmode.ChoiceModeHelperCallback
import kotlinx.android.synthetic.main.fragment_stock_items.*
import org.koin.android.architecture.ext.viewModel

class StockItemsFragment : Fragment(), NewStockDialogFragment.Callback,
    SearchView.OnQueryTextListener {
    private val viewModel by viewModel<StockItemsViewModel>()

    private val fabProvider by lazy { activity as FabProvider }
    private val mainActivity by lazy { activity as? MainActivity }
    private val addStockItemActivity by lazy { activity as? AddStockItemActivity }

    private var actionMode: ActionMode? = null
    private val selectionModeHelper = SelectionModeHelper(object : ChoiceModeHelperCallback {
        override fun onSelectionChanged() {
            updateSelectionCount()
        }

        override fun onEnterSelectionMode() {
            startSelectionMode()
        }

        override fun onExitSelectionMode() {
            actionMode?.finish()
        }
    })
    private val shoppingItemsAdapter =
        StockItemsAdapter(this::onStockItemClicked, selectionModeHelper)

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.stock_items_options_menu, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_stock_items, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        itemsList.layoutManager = LinearLayoutManager(context)
        itemsList.emptyView = emptyView
        itemsList.adapter = shoppingItemsAdapter

        viewModel.stockItemsList.observe(this, Observer {
            shoppingItemsAdapter.updateData(it ?: emptyList())
        })

        fabProvider.configureFab(R.drawable.ic_add_list) {
            val dialog = NewStockDialogFragment()
            dialog.show(childFragmentManager, "newStockItem")
        }
        mainActivity?.title = getString(R.string.title_stock_items)

        savedInstanceState?.let {
            selectionModeHelper.restoreFromState(it)
            if (selectionModeHelper.isInSelectionMode()) {
                startSelectionMode()
                updateSelectionCount()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        selectionModeHelper.saveState(outState)
    }

    override fun onQueryTextSubmit(query: String?) = true
    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.search(newText ?: "")
        return true
    }

    private fun onStockItemClicked(item: StockItem) {
        addStockItemActivity?.finishWithResult(item)
    }

    override fun onNewStockItemCreated(newItem: StockItem) {
        viewModel.createStockItem(newItem)
    }

    private fun startSelectionMode() {
        actionMode = activity?.startActionMode(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.action_confirm -> {
                        onConfirmSelection()
                        return true
                    }
                    R.id.action_delete -> {
                        onDeleteSelection()
                        return true
                    }
                }
                return false
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                mode.menuInflater.inflate(R.menu.menu_context, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val removeItem = if (activity is AddStockItemActivity) {
                    R.id.action_delete
                } else {
                    R.id.action_confirm
                }

                menu?.removeItem(removeItem)
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                selectionModeHelper.cancelSelectionMode()
            }
        })
    }

    private fun updateSelectionCount() {
        actionMode?.title = selectionModeHelper.selectedCount.toString()
    }

    private fun onConfirmSelection() {
        val items = shoppingItemsAdapter.getItems(selectionModeHelper.selectedItems)
        actionMode?.finish()
        addStockItemActivity?.finishWithResult(items.toTypedArray())
    }

    private fun onDeleteSelection() {
        val items = shoppingItemsAdapter.getItems(selectionModeHelper.selectedItems)
        viewModel.deleteStockItems(items)
        actionMode?.finish()
    }
}
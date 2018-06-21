package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.tiagobagni.myshoppinglist.adapter.AdapterItem
import com.tiagobagni.myshoppinglist.extensions.startActivityForResult
import com.tiagobagni.myshoppinglist.stock.AddStockItemActivity
import com.tiagobagni.myshoppinglist.stock.StockItem
import kotlinx.android.synthetic.main.fragment_shopping_list.*
import org.jetbrains.anko.toast
import org.koin.android.architecture.ext.viewModel

class ShoppingListFragment : Fragment(), CommentDialogFragment.Callback {

    private companion object {
        const val ADD_STOCK_ITEM_REQUEST_CODE = 1
    }

    private val shoppingListAdapter = ShoppingListAdapter(this::onItemClicked,
        this::onItemLongClicked)
    private val viewModel by viewModel<ShoppingListViewModel>()
    private val mainActivity by lazy { activity as MainActivity }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_shopping_list, null)

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shopping_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_clear -> {
                viewModel.clear()
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

        shoppingList.layoutManager = LinearLayoutManager(context)
        shoppingList.emptyView = emptyView
        shoppingList.adapter = shoppingListAdapter

        mainActivity.configureFab(R.drawable.ic_add_shopping_cart) {
            startActivityForResult<AddStockItemActivity>(ADD_STOCK_ITEM_REQUEST_CODE)
        }
        mainActivity.title = getString(R.string.title_shopping_list)
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
        viewModel.toggleItemChecked(item)
    }

    private fun onItemLongClicked(item: ShoppingListItem) {
        val commentDialog = CommentDialogFragment.newInstance(item)
        commentDialog.show(childFragmentManager, "comment")
    }

    override fun onCommentEdited(item: ShoppingListItem, comment: String) {
        viewModel.setComment(item, comment)
    }
}
package com.tiagobagni.myshoppinglist.archive

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.tiagobagni.myshoppinglist.ConfirmationDialogFragment
import com.tiagobagni.myshoppinglist.FabProvider
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.extensions.toCurrencyFormat
import com.tiagobagni.myshoppinglist.extensions.toFormattedDate
import kotlinx.android.synthetic.main.fragment_archived_shopping_list.*
import org.koin.android.architecture.ext.viewModel

class ArchivedShoppingListFragment : Fragment(), ConfirmationDialogFragment.Callback {
    companion object {
        const val ARG_TIMESTAMP = "timestamp"
    }

    init {
        setHasOptionsMenu(true)
    }

    private val fabProvider by lazy { activity as FabProvider }
    private val archivedShoppingListAdapter = ArchivedShoppingListAdapter()
    private val timestamp by lazy { arguments?.get(ARG_TIMESTAMP) as Long }

    private val viewModel by viewModel<ArchivedShoppingListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_archived_shopping_list, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fabProvider.hideFab()
        activity?.title = timestamp.toFormattedDate()
        viewModel.getArchivedItemsFrom(timestamp).observe(this, Observer {
            archivedShoppingListAdapter.updateData(it ?: emptyList())
            if (it?.isNotEmpty() == true) {
                val total = it?.map { it.pricePaid }?.reduce { acc, d -> acc + d }
                totalSpent.text = getString(R.string.total_spent, total?.toCurrencyFormat() ?: 0)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        archivedShoppingList.layoutManager = LinearLayoutManager(context)
        archivedShoppingList.emptyView = emptyView
        archivedShoppingList.adapter = archivedShoppingListAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_archived_shopping_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> false
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = ConfirmationDialogFragment.newInstance(
            0,
            getString(R.string.delete_title),
            getString(R.string.delete_archived_msg)
        )

        dialog.show(childFragmentManager, "confirmDelete")
    }

    override fun onConfirmed(dialogId: Int) {
        viewModel.deleteItemsFrom(timestamp)
    }
}
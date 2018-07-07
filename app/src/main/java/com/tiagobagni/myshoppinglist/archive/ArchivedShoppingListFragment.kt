package com.tiagobagni.myshoppinglist.archive

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiagobagni.myshoppinglist.FabProvider
import com.tiagobagni.myshoppinglist.R
import com.tiagobagni.myshoppinglist.extensions.toFormattedDate
import kotlinx.android.synthetic.main.fragment_archived_shopping_list.*
import org.koin.android.architecture.ext.viewModel

class ArchivedShoppingListFragment : Fragment() {

    companion object {
        const val ARG_TIMESTAMP = "timestamp"
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
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        archivedShoppingList.layoutManager = LinearLayoutManager(context)
        archivedShoppingList.emptyView = emptyView
        archivedShoppingList.adapter = archivedShoppingListAdapter
    }
}
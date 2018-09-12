package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiagobagni.myshoppinglist.extensions.hideSoftKeyboard
import kotlinx.android.synthetic.main.fragment_new_shopping_list.*
import org.koin.android.architecture.ext.viewModel

class NewShoppingListFragment : Fragment() {

    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by viewModel<NewShoppingListViewModel>()

    private var existingShoppingLists: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater?.inflate(R.layout.fragment_new_shopping_list, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        shoppingListEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCanCreateList()
            }
        })

        createBtn.setOnClickListener {
            val newListName = getInputListName()
            shoppingListEdit.setText("")
            viewModel.createShoppingList(newListName)
            shoppingListEdit.hideSoftKeyboard()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.existingShoppingLists.observe(this, Observer {
            existingShoppingLists = it?.map { it.listName }
        })

        mainActivity.hideFab()
        mainActivity.title = getString(R.string.new_shopping_list_title)
    }

    private fun updateCanCreateList() {
        val newListName = getInputListName()
        val listAlreadyExist = existingShoppingLists?.contains(newListName) ?: false
        val canCreateNewList = !newListName.isNullOrBlank() && !listAlreadyExist

        createBtn.isEnabled = canCreateNewList
        statusTxt.visibility = if (listAlreadyExist) View.VISIBLE else View.GONE
    }

    private fun getInputListName(): String = shoppingListEdit.text.toString().trim()
}
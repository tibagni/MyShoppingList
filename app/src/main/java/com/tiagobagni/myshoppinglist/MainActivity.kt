package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListFragment
import com.tiagobagni.myshoppinglist.extensions.toFormattedDate
import com.tiagobagni.myshoppinglist.stock.StockItemsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.bundleOf
import org.koin.android.architecture.ext.viewModel

private data class FragmentInfo<T>(val fragmentClass: Class<T>, val tag: String)

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FabProvider {

    private val viewModel by viewModel<MainViewModel>()

    private val fragmentsMap = mutableMapOf(
        R.id.nav_shopping_list to FragmentInfo(
            ShoppingListFragment::class.java,
            "shoppingList"
        ),
        R.id.nav_stock_items to FragmentInfo(
            StockItemsFragment::class.java,
            "stockItems"
        )
    )

    // Maps a timestamp to a menu item
    private val archivedLists = mutableMapOf<Long, Int>()
    // holds the menu item value for the next dynamic menu item created
    // based on archived lists
    private var nextArchivedListMenuItemId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportFragmentManager.addOnBackStackChangedListener(this::fragmentBackStackChanged)
        navView.setNavigationItemSelectedListener(this)
        navigateToStart()

        viewModel.archivedListsTimestamps.observe(this, Observer {
            it?.let { onArchivedListsChanged(it) }
        })
    }

    private fun navigateToStart() {
        navigateToFragment(R.id.nav_shopping_list)
    }

    private fun onArchivedListsChanged(listTimestamps: List<Long>) {
        val newLists = listTimestamps.filterNot { archivedLists.containsKey(it) }
        val removedLists = archivedLists.keys.filterNot { listTimestamps.contains(it) }

        // If the removed list is also the currently selected item,
        // just navigate back to the start
        val currentItemRemoved = removedLists
            .mapNotNull { archivedLists[it] }
            .any { navView.menu.findItem(it)?.isChecked == true }
        if (currentItemRemoved) {
            navigateToStart()
        }

        updateArchivedLists(newLists, removedLists)
    }

    private fun updateArchivedLists(toAdd: List<Long>, toRemove: List<Long>) {
        val archivedListsMenu = navView.menu.findItem(R.id.history_menu_item).subMenu
        toRemove.forEach {
            val itemId = archivedLists[it]
            itemId?.let { archivedListsMenu.removeItem(it) }
            archivedLists.remove(it)
        }

        toAdd.forEach {
            val itemId = nextArchivedListMenuItemId++
            val itemTitle = it.toFormattedDate()
            val itemTag = it.toString()

            val addedItem = archivedListsMenu.add(
                R.id.archived_group,
                itemId,
                Menu.NONE,
                itemTitle
            )

            addedItem.isCheckable = true
            addedItem.isChecked = false
            fragmentsMap[itemId] = FragmentInfo(ArchivedShoppingListFragment::class.java, itemTag)
            archivedLists[it] = itemId
        }
    }

    private fun fragmentBackStackChanged() {
        // Since we support back stack for fragments for better navigation,
        // use it here to correctly set the selected item based on which
        // fragment is on top of the stack
        val topOfStack = supportFragmentManager.backStackEntryCount - 1
        if (topOfStack >= 0) {
            val tag = supportFragmentManager.getBackStackEntryAt(topOfStack)?.name
            tag?.let {
                val keys = fragmentsMap.filterValues { tag == it.tag }.keys
                if (keys.isNotEmpty()) {
                    navView.setCheckedItem(keys.first())
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()

            // If there is no more fragments on the back stack after back was pressed,
            // there is no fragment visible (initial state). In this case, just go
            // back one more time to finish. Otherwise user will have to click back twice
            // and will see a weird empty screen in between
            if (supportFragmentManager.backStackEntryCount == 0) {
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.main, menu)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return if (archivedLists.values.contains(item.itemId)) {
            // We are trying to navigate to an archived list.
            // Provide the timestamp as the argument for the fragment
            val timestamp = archivedLists
                .filter { it.value == item.itemId }
                .map { it.key }
                .first()

            navigateToFragment(
                item.itemId,
                bundleOf(ArchivedShoppingListFragment.ARG_TIMESTAMP to timestamp)
            )
        } else {
            navigateToFragment(item.itemId)
        }
    }

    private fun navigateToFragment(id: Int, args: Bundle? = null): Boolean {
        val fragmentInfo = fragmentsMap[id]
        fragmentInfo?.let {
            val fragment = it.fragmentClass.newInstance()
            showFragment(fragment, it.tag)
            args?.let { fragment.arguments = it }
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        return fragmentInfo != null
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        // If the fragment is already on back stack, just show it instead of keep adding
        // the same thing to the back stack over and over again
        val popped = supportFragmentManager.popBackStackImmediate(tag, 0)
        if (!popped && supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
    }

    override fun configureFab(imageRes: Int?, clickListener: () -> Unit) {
        fab.show()
        fab.setOnClickListener { clickListener() }
        imageRes?.let { fab.setImageResource(imageRes) }
    }

    override fun hideFab() {
        fab.hide()
    }
}

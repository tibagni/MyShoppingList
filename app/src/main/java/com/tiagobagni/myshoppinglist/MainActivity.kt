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
import com.tiagobagni.myshoppinglist.archive.ArchivedListInfo
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListFragment
import com.tiagobagni.myshoppinglist.extensions.toFormattedDate
import com.tiagobagni.myshoppinglist.settings.SettingsFragment
import com.tiagobagni.myshoppinglist.stock.StockItemsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.bundleOf
import org.koin.android.architecture.ext.viewModel

private data class FragmentInfo<T>(val fragmentClass: Class<T>, val tag: String)

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FabProvider {

    private companion object {
        private const val INITIAL_NAVIGATION_KEY = "initialNavigation"
    }

    private val viewModel by viewModel<MainViewModel>()

    private val fragmentsMap = mutableMapOf(
        R.id.nav_create_shopping_list to FragmentInfo(
            NewShoppingListFragment::class.java,
            "newShoppingList"
        ),
        R.id.nav_stock_items to FragmentInfo(
            StockItemsFragment::class.java,
            "stockItems"
        ),
        R.id.nav_settings to FragmentInfo(
            SettingsFragment::class.java,
            "settings"
        )
    )

    // Maps a shopping list to a menu item id
    private val activeLists = mutableMapOf<ShoppingList, Int>()

    // Maps an archived shopping list to a menu item id
    private val archivedLists = mutableMapOf<ArchivedListInfo, Int>()

    // holds the menu item value for the next dynamic menu item created
    private var nextDynamicMenuItemId = 1

    // Flag used to control whether or not it is necessary to navigate to start
    // when lists data is ready. We only navigate to start on the very first time
    private var hasPerformedInitialNavigation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        hasPerformedInitialNavigation = savedInstanceState?.getBoolean(
            INITIAL_NAVIGATION_KEY,
            false
        ) ?: false

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

        viewModel.activeLists.observe(this, Observer {
            it?.let { onActiveListsChanged(it) }

            if (!hasPerformedInitialNavigation) {
                hasPerformedInitialNavigation = true
                navigateToStart()
            }
        })

        viewModel.archivedListsTimestamps.observe(this, Observer {
            it?.let { onArchivedListsChanged(it) }
        })
    }

    private fun navigateToStart() {
        // If there are Lists created, navigate to the first one available,
        // otherwise navigate to the 'New List' screen
        if (activeLists.isEmpty()) {
            navigateToFragment(R.id.nav_create_shopping_list)
        } else {
            val (list, itemId) = activeLists.toList().first()
            navigateToFragment(
                itemId,
                bundleOf(ShoppingListFragment.ARG_LIST_NAME to list.listName)
            )
        }
    }

    private fun onActiveListsChanged(newActiveLists: List<ShoppingList>) {
        val (addedLists, removedLists) = onDynamicItemsChanged(newActiveLists, activeLists)

        val menu = navView.menu.findItem(R.id.active_lists_menu_item).subMenu
        updateDynamicMenuItems(
            addedLists,
            removedLists,
            activeLists,
            ShoppingListFragment::class.java,
            { shoppingList -> shoppingList.listName },
            menu,
            R.id.active_group
        )

        // The only situation where there is a new active list is when it is created
        // And we need to navigate to the newly created list in this case
        if (addedLists.size == 1) {
            val newActiveListName = addedLists[0].listName
            val newActiveListItemId = activeLists
                .filter { it.key.listName == newActiveListName }
                .map { it.value }
                .first()

            navigateToFragment(
                newActiveListItemId,
                bundleOf(ShoppingListFragment.ARG_LIST_NAME to newActiveListName)
            )
        }
    }

    private fun onArchivedListsChanged(newArchivedLists: List<ArchivedListInfo>) {
        val (addedLists, removedLists) = onDynamicItemsChanged(newArchivedLists, archivedLists)

        val menu = navView.menu.findItem(R.id.history_menu_item).subMenu
        val titleFor = { info: ArchivedListInfo ->
            getString(
                R.string.archived_list_title,
                info.listName,
                info.archiveTimestamp.toFormattedDate()
            )
        }

        updateDynamicMenuItems(
            addedLists,
            removedLists,
            archivedLists,
            ArchivedShoppingListFragment::class.java,
            titleFor,
            menu,
            R.id.archived_group
        )
    }

    private fun <E> onDynamicItemsChanged(
        newItems: List<E>,
        currentItems: Map<E, Int>
    ): Pair<List<E>, List<E>> {
        val addedItems = newItems.filterNot { currentItems.containsKey(it) }
        val removedItems = currentItems.keys.filterNot { newItems.contains(it) }

        return addedItems to removedItems
    }

    private fun <E> updateDynamicMenuItems(
        toAdd: List<E>,
        toRemove: List<E>,
        allItems: MutableMap<E, Int>,
        fragmentClass: Class<out Fragment>,
        titleFor: (E) -> String,
        menu: Menu,
        groupId: Int
    ) {
        // If the removed item is also the currently selected item,
        // just navigate back to the start
        val navigateToStart = toRemove
            .mapNotNull { allItems[it] }
            .any { navView.menu.findItem(it)?.isChecked == true }

        toRemove.forEach {
            val itemId = allItems[it]
            itemId?.let { menu.removeItem(it) }
            allItems.remove(it)
        }

        toAdd.forEach {
            val itemId = nextDynamicMenuItemId++
            val itemTitle = titleFor(it)
            val itemTag = it.toString()

            val addedItem = menu.add(
                groupId,
                itemId,
                Menu.NONE,
                itemTitle
            )

            addedItem.isCheckable = true
            addedItem.isChecked = false
            fragmentsMap[itemId] = FragmentInfo(fragmentClass, itemTag)
            allItems[it] = itemId
        }

        // Navigate to start if needed
        if (navigateToStart) {
            navigateToStart()
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(INITIAL_NAVIGATION_KEY, hasPerformedInitialNavigation)
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
        // Some menu items need to pass arguments to the target fragment,
        // get the arguments here if that is the case
        val args = when {
            archivedLists.values.contains(item.itemId) -> {
                val info = getDynamicItemInfo(archivedLists, item.itemId)
                bundleOf(
                    ArchivedShoppingListFragment.ARG_TIMESTAMP to info.archiveTimestamp,
                    ArchivedShoppingListFragment.ARG_LIST_NAME to info.listName
                )
            }
            activeLists.values.contains(item.itemId) -> {
                val info = getDynamicItemInfo(activeLists, item.itemId)
                bundleOf(ShoppingListFragment.ARG_LIST_NAME to info.listName)
            }
            else -> null
        }

        return navigateToFragment(item.itemId, args)
    }

    private fun <E> getDynamicItemInfo(dynamicItems: Map<E, Int>, menuItemId: Int): E {
        return dynamicItems
            .filter { it.value == menuItemId }
            .map { it.key }
            .first()
    }

    private fun navigateToFragment(id: Int, args: Bundle? = null): Boolean {
        val fragmentInfo = fragmentsMap[id]
        fragmentInfo?.let {
            val fragment = it.fragmentClass.newInstance() as Fragment
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

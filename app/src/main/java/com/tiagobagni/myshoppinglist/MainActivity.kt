package com.tiagobagni.myshoppinglist

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tiagobagni.myshoppinglist.stock.StockItemsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

private val FRAGMENT_MAP = mapOf(
    R.id.nav_shopping_list to ShoppingListFragment::class.java,
    R.id.nav_stock_items to StockItemsFragment::class.java
)

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FabProvider {

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
        navigateToFragment(R.id.nav_shopping_list)
    }

    private fun fragmentBackStackChanged() {
        // Since we support back stack for fragments for better navigation,
        // use it here to correctly set the selected item based on which
        // fragment is on top of the stack
        val topOfStack = supportFragmentManager.backStackEntryCount - 1
        if (topOfStack >= 0) {
            val tag = supportFragmentManager.getBackStackEntryAt(topOfStack)?.name
            tag?.let {
                val keys = FRAGMENT_MAP.filterValues { tag == it.name }.keys
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
        return navigateToFragment(item.itemId)
    }

    private fun navigateToFragment(id: Int): Boolean {
        val fragment = FRAGMENT_MAP[id]?.newInstance()

        fragment?.let { showFragment(it) }
        drawerLayout.closeDrawer(GravityCompat.START)
        return fragment != null
    }

    private fun showFragment(fragment: Fragment) {
        val tag = fragment.javaClass.name

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
        fab.setOnClickListener { clickListener() }
        imageRes?.let { fab.setImageResource(imageRes) }
    }
}

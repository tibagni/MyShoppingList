package com.tiagobagni.myshoppinglist.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * A RecyclerView that shows an emptyView when it is empty
 */
class EmptyableRecyclerView : RecyclerView {
    var emptyView: View? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setAdapter(adapter: Adapter<*>?) {
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                updateEmptyView(adapter)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                updateEmptyView(adapter)
            }
        })
        super.setAdapter(adapter)
        adapter?.let {
            updateEmptyView(adapter)
        }
    }

    private fun updateEmptyView(adapter: Adapter<*>) {
        val isEmpty = adapter.itemCount == 0
        emptyView?.visibility = if (isEmpty) VISIBLE else GONE
    }

}
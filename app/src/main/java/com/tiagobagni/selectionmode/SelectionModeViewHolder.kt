package com.tiagobagni.selectionmode

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class SelectionModeViewHolder<in T>(
    view: View,
    private val selectionModeHelper: SelectionModeHelper,
    private var clickListener: SelectionModeVHClickListener? = null
) :
    RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
    fun bind(item: T) {
        onBind(item)
        itemView.isSelected = selectionModeHelper.isItemSelected(layoutPosition)
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    override fun onClick(view: View) {
        if (selectionModeHelper.isInSelectionMode()) {
            view.isSelected = selectionModeHelper.toggleItemSelection(layoutPosition)
        } else {
            clickListener?.onClick(layoutPosition, view)
        }
    }

    override fun onLongClick(view: View): Boolean {
        if (selectionModeHelper.isInSelectionMode()) {
            return false
        }

        view.isSelected = selectionModeHelper.toggleItemSelection(layoutPosition)
        return true
    }

    abstract fun onBind(item: T)
}

interface SelectionModeVHClickListener{
    fun onClick(position: Int, view: View)
}
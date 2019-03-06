package com.tiagobagni.myshoppinglist.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

abstract class RecyclerListAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val bgExecutor = Executors.newSingleThreadExecutor()

    protected var list = emptyList<T>()
        private set

    fun updateData(data: List<T>) {
        val newData = onPrepareNewData(data)
        if (list == newData) {
            return
        }

        if (list.isEmpty()) {
            list = newData
            notifyItemRangeInserted(0, newData.size)
            return
        }

        if (newData.isEmpty()) {
            val oldSize = list.size
            list = emptyList()
            notifyItemRangeRemoved(0, oldSize)
            return
        }

        // The lists here will not be very big, so we don't need to offload
        // DiffUtil calculations to a background thread. Keeping it all
        // synchronous will keep the logic simple
        val diff = DiffUtil.calculateDiff(DiffCallback(list, newData))
        list = newData
        diff.dispatchUpdatesTo(this)
    }

    /**
     * Called right before updating the data.
     * If any manipulation is needed, this is the right time
     */
    open fun onPrepareNewData(newData: List<T>) = newData

    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    /**
     * By default, it is the same as 'areItemsTheSame'
     */
    open fun areContentsTheSame(oldItem: T, newItem: T) = areItemsTheSame(oldItem, newItem)

    override fun getItemCount() = list.size

    private inner class DiffCallback(val adapterList: List<T>, val newList: List<T>) :
        DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areItemsTheSame(adapterList[oldItemPosition], newList[newItemPosition])

        override fun getOldListSize(): Int = adapterList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean = areContentsTheSame(adapterList[oldItemPosition], newList[newItemPosition])
    }
}
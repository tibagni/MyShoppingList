package com.tiagobagni.myshoppinglist.extensions

typealias ClickListener<T> = (T) -> Unit

interface AdapterClickListener {
    fun onItemClicked(itemPosition: Int)
}
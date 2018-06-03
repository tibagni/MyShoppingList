package com.tiagobagni.myshoppinglist.adapter

data class AdapterItem<out T>(val value: T?, val viewType: Int)
package com.tiagobagni.myshoppinglist

interface FabProvider {
    fun configureFab(imageRes: Int? = null, clickListener: () -> Unit)
}
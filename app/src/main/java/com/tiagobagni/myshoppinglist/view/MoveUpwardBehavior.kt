package com.tiagobagni.myshoppinglist.view

import android.content.Context
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.view.View
import com.google.android.material.snackbar.Snackbar
import android.util.AttributeSet


class MoveUpwardBehavior(context: Context, attrs: AttributeSet) :
    AppBarLayout.ScrollingViewBehavior(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return super.layoutDependsOn(parent, child, dependency) or
                (dependency is Snackbar.SnackbarLayout)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (dependency is Snackbar.SnackbarLayout) {
            val translationY = Math.min(0f, dependency.translationY - dependency.height)

            // Move entire child layout up that causes objects on top disappear
            child.translationY = translationY

            // Set top padding to child layout to reappear missing objects
            // If you had set padding to child in xml, then you have to set them here by
            // <child.getPaddingLeft(), ...>
            child.setPadding(0, -Math.round(translationY), 0, 0);
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }
}
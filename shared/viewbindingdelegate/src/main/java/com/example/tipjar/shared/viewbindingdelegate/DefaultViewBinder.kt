package com.example.tipjar.shared.viewbindingdelegate

import android.view.View
import androidx.viewbinding.ViewBinding


@PublishedApi
internal class DefaultViewBinder<T : ViewBinding>(
    private val viewBindingClass: Class<T>
) : ViewBinder<T> {

    /**
     * Cache static method `ViewBinding.bind(View)`
     */
    private val bindViewMethod by lazy(LazyThreadSafetyMode.NONE) {
        viewBindingClass.getMethod("bind", View::class.java)
    }

    /**
     * Create new [ViewBinding] instance
     */
    @Suppress("UNCHECKED_CAST")
    override fun bind(view: View): T {
        return bindViewMethod(null, view) as T
    }
}
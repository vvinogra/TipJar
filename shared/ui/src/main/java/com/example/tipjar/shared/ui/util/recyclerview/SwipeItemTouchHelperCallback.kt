package com.example.tipjar.shared.ui.util.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.tipjar.shared.ui.extensions.toPx


class SwipeItemTouchHelperCallback(
    context: Context,
    @DrawableRes private val iconRes: Int,
    @ColorInt private val backgroundColor: Int,
    private val removeItemCallback: (RecyclerView.ViewHolder) -> Unit
) : ItemTouchHelper.Callback() {

    companion object {
        private const val HORIZONTAL_ICON_MARGIN_IN_DP = 16

        // So background is behind the rounded corners of itemView
        private const val BACKGROUND_CORNER_OFFSET = 20
    }

    private val icon = ContextCompat.getDrawable(context, iconRes)!!
    private val background = ColorDrawable(backgroundColor)

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
        removeItemCallback.invoke(viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        val iconHorizontalMargin = HORIZONTAL_ICON_MARGIN_IN_DP.toPx()
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (dX > 0) { // Swiping to the right
            val iconLeft = itemView.left + iconHorizontalMargin
            val iconRight = iconLeft + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.left, itemView.top,
                itemView.left + dX.toInt() + BACKGROUND_CORNER_OFFSET, itemView.bottom
            )
        } else if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconHorizontalMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconHorizontalMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.right + dX.toInt() - BACKGROUND_CORNER_OFFSET,
                itemView.top, itemView.right, itemView.bottom
            )
        } else { // View is unSwiped
            background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon.draw(c)
    }
}
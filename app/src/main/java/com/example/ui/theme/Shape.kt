package com.example.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object ExpressiveShapes {
    // Standalone Buttons: Fully rounded pill shape (percent = 50)
    val standaloneButton = RoundedCornerShape(percent = 50)

    // Single cards have 16dp on all corners
    val singleCard = RoundedCornerShape(16.dp)

    /**
     * Cards in grouped lists:
     * - Outer card corners are 16dp
     * - Inner adjacent corners are 4dp
     * - Spacing between cards is 4dp
     * - Single cards have 16dp on all corners
     */
    fun groupedCardShape(index: Int, total: Int): RoundedCornerShape {
        return when {
            total <= 1 -> singleCard
            index == 0 -> RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
            index == total - 1 -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
            else -> RoundedCornerShape(4.dp)
        }
    }

    /**
     * Segmented Selector:
     * - Selected segment: Fully rounded corners (percent = 50)
     * - Unselected segment: Inner corners connected to other items must be 4dp,
     *   outer exposed corners must be fully rounded (percent = 50)
     */
    fun selectorSegmentShape(index: Int, total: Int, isSelected: Boolean): RoundedCornerShape {
        if (isSelected) {
            return RoundedCornerShape(percent = 50)
        }
        return when {
            total <= 1 -> RoundedCornerShape(percent = 50)
            index == 0 -> RoundedCornerShape(
                topStart = CornerSize(50),
                bottomStart = CornerSize(50),
                topEnd = CornerSize(4.dp),
                bottomEnd = CornerSize(4.dp)
            )
            index == total - 1 -> RoundedCornerShape(
                topStart = CornerSize(4.dp),
                bottomStart = CornerSize(4.dp),
                topEnd = CornerSize(50),
                bottomEnd = CornerSize(50)
            )
            else -> RoundedCornerShape(4.dp)
        }
    }
}

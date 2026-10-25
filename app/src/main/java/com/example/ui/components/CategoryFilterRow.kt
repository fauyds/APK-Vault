package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CategoryEntity
import com.example.model.CategoryFilter
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryFilterRow(
    categories: List<CategoryEntity>,
    selectedFilter: CategoryFilter,
    categoryCounts: Map<Long?, Int>,
    totalCount: Int,
    uncategorizedCount: Int,
    onSelectFilter: (CategoryFilter) -> Unit,
    onLongClickCategory: (CategoryEntity) -> Unit,
    onAddCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = buildList {
        add(CategoryFilter.All)
        add(CategoryFilter.Uncategorized)
        categories.forEach { add(CategoryFilter.Custom(it)) }
    }
    val total = filters.size
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp), // Spacing between chips: 4dp
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEachIndexed { index, filter ->
            val isSelected = when (filter) {
                is CategoryFilter.All -> selectedFilter is CategoryFilter.All
                is CategoryFilter.Uncategorized -> selectedFilter is CategoryFilter.Uncategorized
                is CategoryFilter.Custom -> selectedFilter is CategoryFilter.Custom && selectedFilter.category.id == filter.category.id
            }

            val shape = ExpressiveShapes.selectorSegmentShape(index, total, isSelected)
            val backgroundColor = if (isSelected) {
                ExpressiveTheme.colors.buttonPrimary
            } else {
                ExpressiveTheme.colors.cardSurface
            }
            val contentColor = if (isSelected) {
                ExpressiveTheme.colors.onButtonPrimary
            } else {
                ExpressiveTheme.colors.textPrimary
            }

            val count = when (filter) {
                is CategoryFilter.All -> totalCount
                is CategoryFilter.Uncategorized -> uncategorizedCount
                is CategoryFilter.Custom -> categoryCounts[filter.category.id] ?: 0
            }

            Box(
                modifier = Modifier
                    .height(38.dp)
                    .clip(shape)
                    .background(backgroundColor)
                    .combinedClickable(
                        onClick = { onSelectFilter(filter) },
                        onLongClick = {
                            if (filter is CategoryFilter.Custom) {
                                onLongClickCategory(filter.category)
                            }
                        }
                    )
                    .padding(horizontal = 12.dp)
                    .testTag("category_chip_${filter.displayName.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = filter.displayName,
                        color = contentColor,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )

                    if (count > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) ExpressiveTheme.colors.nestedElementSurface
                                    else ExpressiveTheme.colors.nestedElementSurface
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                color = if (isSelected) ExpressiveTheme.colors.textPrimary
                                else ExpressiveTheme.colors.textSecondary,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Persistent "+ Add" button at the end of the row
        Box(
            modifier = Modifier
                .height(38.dp)
                .clip(ExpressiveShapes.standaloneButton)
                .background(ExpressiveTheme.colors.cardSurface)
                .combinedClickable(onClick = onAddCategory)
                .padding(horizontal = 12.dp)
                .testTag("add_category_chip_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Category",
                    tint = ExpressiveTheme.colors.buttonPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Add",
                    color = ExpressiveTheme.colors.buttonPrimary,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

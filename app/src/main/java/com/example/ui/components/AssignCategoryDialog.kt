package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LabelOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CategoryEntity
import com.example.model.ApkVaultItem
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@Composable
fun AssignCategoryDialog(
    item: ApkVaultItem,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onRequestCreateCategory: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ExpressiveShapes.singleCard,
        containerColor = ExpressiveTheme.colors.cardSurface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Assign Category",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ExpressiveTheme.colors.textPrimary
                    )
                )
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ExpressiveTheme.colors.textSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Option 1: Uncategorized (No Category)
                    item {
                        val isSelected = item.categoryId == null
                        CategoryOptionRow(
                            name = "Uncategorized",
                            icon = Icons.Default.LabelOff,
                            isSelected = isSelected,
                            onClick = { onCategorySelected(null) }
                        )
                    }

                    // Existing categories
                    items(categories, key = { it.id }) { cat ->
                        val isSelected = item.categoryId == cat.id
                        CategoryOptionRow(
                            name = cat.name,
                            icon = Icons.Default.Label,
                            isSelected = isSelected,
                            onClick = { onCategorySelected(cat.id) }
                        )
                    }
                }

                // Add New Category Quick Action
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ExpressiveShapes.standaloneButton)
                        .background(ExpressiveTheme.colors.nestedElementSurface)
                        .clickable { onRequestCreateCategory() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("dialog_add_category_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = ExpressiveTheme.colors.buttonPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Add New Category",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ExpressiveTheme.colors.buttonPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = ExpressiveShapes.standaloneButton
            ) {
                Text(
                    "Cancel",
                    color = ExpressiveTheme.colors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun CategoryOptionRow(
    name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) ExpressiveTheme.colors.nestedElementSurface
                else ExpressiveTheme.colors.cardSurface
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) ExpressiveTheme.colors.buttonPrimary
                else ExpressiveTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = ExpressiveTheme.colors.textPrimary
                ),
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = ExpressiveTheme.colors.buttonPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

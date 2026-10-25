package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.data.entity.CategoryEntity
import com.example.data.repository.IconCatalogRepository
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun IconPickerSection(
    selectedIconName: String,
    onIconSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember(context) { IconCatalogRepository(context) }

    var allIcons by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingCatalog by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredIcons by remember { mutableStateOf<List<String>>(emptyList()) }
    var isFiltering by remember { mutableStateOf(false) }

    // Fetch full 3,000+ catalog on launch
    LaunchedEffect(Unit) {
        isLoadingCatalog = true
        val icons = repository.getIconCatalog()
        allIcons = icons
        filteredIcons = icons
        isLoadingCatalog = false
    }

    // Debounced search filtering (250ms)
    LaunchedEffect(searchQuery, allIcons) {
        if (allIcons.isEmpty()) return@LaunchedEffect
        val query = searchQuery.trim().lowercase()
        if (query.isEmpty()) {
            filteredIcons = allIcons
            isFiltering = false
        } else {
            isFiltering = true
            delay(250) // 250ms debounce
            withContext(Dispatchers.Default) {
                val matches = allIcons.filter { it.contains(query) }
                withContext(Dispatchers.Main) {
                    filteredIcons = matches
                    isFiltering = false
                }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Search Bar for Icons
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                val countText = if (allIcons.isNotEmpty()) "${allIcons.size}+" else "3,000+"
                Text("Search $countText Material icons...", fontSize = 13.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ExpressiveTheme.colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = ExpressiveTheme.colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = ExpressiveTheme.colors.textPrimary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ExpressiveTheme.colors.buttonPrimary,
                unfocusedBorderColor = ExpressiveTheme.colors.nestedElementSurface,
                focusedContainerColor = ExpressiveTheme.colors.nestedElementSurface,
                unfocusedContainerColor = ExpressiveTheme.colors.nestedElementSurface,
                cursorColor = ExpressiveTheme.colors.buttonPrimary
            ),
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("icon_search_input")
        )

        if (searchQuery.isNotBlank() && !isFiltering) {
            Text(
                text = "${filteredIcons.size} icon${if (filteredIcons.size == 1) "" else "s"} found",
                style = MaterialTheme.typography.labelSmall.copy(color = ExpressiveTheme.colors.textSecondary),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // 5-Column Grid with 4dp Spacing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 210.dp)
        ) {
            if (isLoadingCatalog || isFiltering) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = ExpressiveTheme.colors.buttonPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            text = if (isLoadingCatalog) "Loading Material Icons catalog..." else "Filtering icons...",
                            style = MaterialTheme.typography.labelMedium.copy(color = ExpressiveTheme.colors.textSecondary)
                        )
                    }
                }
            } else if (filteredIcons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No icons found for '$searchQuery'",
                        style = MaterialTheme.typography.bodySmall.copy(color = ExpressiveTheme.colors.textSecondary)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    contentPadding = PaddingValues(2.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("icon_picker_grid")
                ) {
                    items(
                        items = filteredIcons,
                        key = { it }
                    ) { iconName ->
                        val isSelected = CategoryIconHelper.toSymbolName(iconName) == CategoryIconHelper.toSymbolName(selectedIconName)

                        // Shape: 16dp rounded corners for unselected, Circular for selected
                        val cellShape = if (isSelected) CircleShape else RoundedCornerShape(16.dp)
                        val bgColor = if (isSelected) ExpressiveTheme.colors.buttonPrimary else ExpressiveTheme.colors.nestedElementSurface
                        val tintColor = if (isSelected) ExpressiveTheme.colors.onButtonPrimary else ExpressiveTheme.colors.textPrimary

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(cellShape)
                                .background(bgColor)
                                .clickable { onIconSelected(iconName) }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CategoryIconView(
                                iconName = iconName,
                                tint = tintColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, iconName: String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("folder") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ExpressiveShapes.singleCard,
        containerColor = ExpressiveTheme.colors.cardSurface,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(ExpressiveShapes.standaloneButton)
                    .background(ExpressiveTheme.colors.nestedElementSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = ExpressiveTheme.colors.buttonPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "New Category",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpressiveTheme.colors.textPrimary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ExpressiveTheme.colors.buttonPrimary,
                        unfocusedBorderColor = ExpressiveTheme.colors.textSecondary,
                        focusedLabelColor = ExpressiveTheme.colors.buttonPrimary,
                        cursorColor = ExpressiveTheme.colors.buttonPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("create_category_name_input")
                )

                Text(
                    text = "Select Material Icon",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ExpressiveTheme.colors.textSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                IconPickerSection(
                    selectedIconName = selectedIconName,
                    onIconSelected = { selectedIconName = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onConfirm(categoryName.trim(), selectedIconName)
                    }
                },
                enabled = categoryName.isNotBlank(),
                shape = ExpressiveShapes.standaloneButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExpressiveTheme.colors.buttonPrimary,
                    contentColor = ExpressiveTheme.colors.onButtonPrimary
                ),
                modifier = Modifier.testTag("confirm_create_category")
            ) {
                Text("Create", fontWeight = FontWeight.Bold)
            }
        },
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
fun RenameCategoryDialog(
    category: CategoryEntity,
    onDismiss: () -> Unit,
    onConfirm: (newName: String) -> Unit
) {
    var newName by remember { mutableStateOf(category.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ExpressiveShapes.singleCard,
        containerColor = ExpressiveTheme.colors.cardSurface,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(ExpressiveShapes.standaloneButton)
                    .background(ExpressiveTheme.colors.nestedElementSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = ExpressiveTheme.colors.buttonPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "Rename Category",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpressiveTheme.colors.textPrimary
                )
            )
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Category Name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ExpressiveTheme.colors.buttonPrimary,
                    unfocusedBorderColor = ExpressiveTheme.colors.textSecondary,
                    focusedLabelColor = ExpressiveTheme.colors.buttonPrimary,
                    cursorColor = ExpressiveTheme.colors.buttonPrimary
                ),
                modifier = Modifier.fillMaxWidth().testTag("rename_category_input")
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.isNotBlank()) {
                        onConfirm(newName.trim())
                    }
                },
                enabled = newName.isNotBlank(),
                shape = ExpressiveShapes.standaloneButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExpressiveTheme.colors.buttonPrimary,
                    contentColor = ExpressiveTheme.colors.onButtonPrimary
                ),
                modifier = Modifier.testTag("confirm_rename_category")
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = ExpressiveShapes.standaloneButton
            ) {
                Text("Cancel", color = ExpressiveTheme.colors.textSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun ChangeCategoryIconDialog(
    category: CategoryEntity,
    onDismiss: () -> Unit,
    onConfirm: (newIconName: String) -> Unit
) {
    var selectedIconName by remember { mutableStateOf(category.iconName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ExpressiveShapes.singleCard,
        containerColor = ExpressiveTheme.colors.cardSurface,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(ExpressiveShapes.standaloneButton)
                    .background(ExpressiveTheme.colors.nestedElementSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = ExpressiveTheme.colors.buttonPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "Change Icon",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpressiveTheme.colors.textPrimary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Search or select a new Material symbol:",
                    style = MaterialTheme.typography.bodySmall.copy(color = ExpressiveTheme.colors.textSecondary)
                )

                IconPickerSection(
                    selectedIconName = selectedIconName,
                    onIconSelected = { selectedIconName = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedIconName) },
                shape = ExpressiveShapes.standaloneButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExpressiveTheme.colors.buttonPrimary,
                    contentColor = ExpressiveTheme.colors.onButtonPrimary
                ),
                modifier = Modifier.testTag("apply_change_icon")
            ) {
                Text("Apply", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = ExpressiveShapes.standaloneButton
            ) {
                Text("Cancel", color = ExpressiveTheme.colors.textSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun DeleteCategoryDialog(
    category: CategoryEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ExpressiveShapes.singleCard,
        containerColor = ExpressiveTheme.colors.cardSurface,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(ExpressiveShapes.standaloneButton)
                    .background(ExpressiveTheme.colors.nestedElementSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = ExpressiveTheme.colors.errorColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "Delete Category?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpressiveTheme.colors.textPrimary
                )
            )
        },
        text = {
            Text(
                text = "Deleting '${category.name}' will NOT delete your APK files. All APKs in this category will be moved to 'Uncategorized'.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ExpressiveTheme.colors.textSecondary,
                    lineHeight = 20.sp
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = ExpressiveShapes.standaloneButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExpressiveTheme.colors.buttonTertiarySubtle,
                    contentColor = ExpressiveTheme.colors.onButtonTertiarySubtle
                ),
                modifier = Modifier.testTag("confirm_delete_category")
            ) {
                Text("Delete Category", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = ExpressiveShapes.standaloneButton
            ) {
                Text("Cancel", color = ExpressiveTheme.colors.textSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun AssignCategoryDialog(
    appName: String,
    currentCategoryId: Long?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onCategorySelected: (Long?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ExpressiveShapes.singleCard,
        containerColor = ExpressiveTheme.colors.cardSurface,
        title = {
            Text(
                text = "Assign Category",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpressiveTheme.colors.textPrimary
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select a category for $appName:",
                    style = MaterialTheme.typography.bodySmall.copy(color = ExpressiveTheme.colors.textSecondary)
                )

                // Option: Uncategorized
                val isUncategorized = currentCategoryId == null || currentCategoryId <= 0L
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isUncategorized) ExpressiveTheme.colors.buttonPrimary.copy(alpha = 0.15f)
                            else ExpressiveTheme.colors.nestedElementSurface
                        )
                        .clickable { onCategorySelected(null) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Uncategorized",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isUncategorized) FontWeight.Bold else FontWeight.Normal,
                            color = ExpressiveTheme.colors.textPrimary
                        )
                    )
                }

                // Custom Categories
                categories.forEach { cat ->
                    val isSelected = currentCategoryId == cat.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) ExpressiveTheme.colors.buttonPrimary.copy(alpha = 0.15f)
                                else ExpressiveTheme.colors.nestedElementSurface
                            )
                            .clickable { onCategorySelected(cat.id) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CategoryIconView(
                                iconName = cat.iconName,
                                tint = ExpressiveTheme.colors.buttonPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = ExpressiveTheme.colors.textPrimary
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ExpressiveTheme.colors.textSecondary)
            }
        }
    )
}

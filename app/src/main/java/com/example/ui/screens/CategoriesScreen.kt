package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CategoryEntity
import com.example.model.ApkVaultItem
import com.example.ui.components.CategoryIconView
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme
import com.example.util.ApkScanner

const val CATEGORY_ID_ALL = -1L
const val CATEGORY_ID_UNCATEGORIZED = -2L

@Composable
fun CategoriesScreen(
    categories: List<CategoryEntity>,
    apkItems: List<ApkVaultItem>,
    categoryMappings: Map<String, Long>,
    isScanning: Boolean,
    onCategoryClick: (categoryId: Long, categoryName: String) -> Unit,
    onCreateCategoryClick: () -> Unit,
    onRenameCategoryClick: (CategoryEntity) -> Unit,
    onChangeIconClick: (CategoryEntity) -> Unit,
    onDeleteCategoryClick: (CategoryEntity) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenExtractor: () -> Unit,
    onRescan: () -> Unit,
    onExportBackup: (Uri) -> Unit = {},
    onImportBackup: (Uri) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalApks = apkItems.size
    val totalSizeBytes = apkItems.sumOf { it.sizeBytes }
    val totalSizeFormatted = ApkScanner.formatFileSize(totalSizeBytes)
    val totalCategoriesCount = categories.size

    var backupMenuExpanded by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { onExportBackup(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onImportBackup(it) }
    }

    // Calculate APK counts per category
    val allCount = totalApks
    val uncategorizedCount = apkItems.count { item ->
        val catId = categoryMappings[item.packageName]
        catId == null || catId <= 0L
    }

    val categoryCounts = remember(apkItems, categoryMappings, categories) {
        categories.associate { cat ->
            cat.id to apkItems.count { item -> categoryMappings[item.packageName] == cat.id }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExpressiveTheme.colors.appBackground)
    ) {
        // 1. TOP HEADER CARD ("Apk Vault - Local APK Vault & Extractor"):
        // Outer top corners 16dp, inner bottom corners 4dp
        val topCardShape = ExpressiveShapes.groupedCardShape(0, 2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp) // 2dp bottom padding
                .clip(topCardShape)
                .background(ExpressiveTheme.colors.cardSurface)
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("categories_header_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(ExpressiveShapes.standaloneButton)
                        .background(ExpressiveTheme.colors.nestedElementSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = ExpressiveTheme.colors.buttonPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "APK Vault",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ExpressiveTheme.colors.textPrimary,
                            letterSpacing = (-0.2).sp
                        )
                    )
                    Text(
                        text = "Local APK Vault & Extractor",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ExpressiveTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                // Search Button
                IconButton(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("search_categories_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search APKs",
                        tint = ExpressiveTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Extractor Button
                IconButton(
                    onClick = onOpenExtractor,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("installed_extractor_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = "Installed Apps Extractor",
                        tint = ExpressiveTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Rescan Button
                IconButton(
                    onClick = onRescan,
                    enabled = !isScanning,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("rescan_vault_button")
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ExpressiveTheme.colors.buttonPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rescan Vault",
                            tint = ExpressiveTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Backup & Restore Menu Button
                Box {
                    IconButton(
                        onClick = { backupMenuExpanded = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("backup_restore_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = "Backup and Restore",
                            tint = ExpressiveTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = backupMenuExpanded,
                        onDismissRequest = { backupMenuExpanded = false },
                        modifier = Modifier.background(ExpressiveTheme.colors.cardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export Backup") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                backupMenuExpanded = false
                                exportLauncher.launch("apk_vault_backup.json")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Import / Restore") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                backupMenuExpanded = false
                                importLauncher.launch(arrayOf("application/json", "*/*"))
                            }
                        )
                    }
                }
            }
        }

        // 2. SUMMARY CARD:
        // Distance from top card is 4dp (2dp bottom padding + 2dp top padding).
        // Inner top corners 4dp, outer bottom corners 16dp.
        val summaryCardShape = ExpressiveShapes.groupedCardShape(1, 2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 4.dp)
                .clip(summaryCardShape)
                .background(ExpressiveTheme.colors.cardSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("categories_summary_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Storage Size
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SdStorage,
                        contentDescription = null,
                        tint = ExpressiveTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = totalSizeFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExpressiveTheme.colors.textSecondary
                        )
                    )
                }

                // Category Count
                Text(
                    text = "$totalCategoriesCount Categories",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }

        // 3. VERTICAL CATEGORY LIST:
        // A strict vertical LazyColumn with 4dp spacing between items. Strictly NO horizontal scrolling.
        val totalCategoryItems = 2 + categories.size

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("categories_vertical_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp) // 4dp rhythm spacing
        ) {
            // Pinned System Category 1: "All APKs"
            item(key = "system_all_apks") {
                CategoryItemCard(
                    iconName = "folder",
                    name = "All APKs",
                    apkCount = allCount,
                    isSystem = true,
                    onClick = { onCategoryClick(CATEGORY_ID_ALL, "All APKs") },
                    shape = ExpressiveShapes.groupedCardShape(0, totalCategoryItems)
                )
            }

            // Pinned System Category 2: "Uncategorized"
            item(key = "system_uncategorized") {
                CategoryItemCard(
                    iconName = "folder_open",
                    name = "Uncategorized",
                    apkCount = uncategorizedCount,
                    isSystem = true,
                    onClick = { onCategoryClick(CATEGORY_ID_UNCATEGORIZED, "Uncategorized") },
                    shape = ExpressiveShapes.groupedCardShape(1, totalCategoryItems)
                )
            }

            // Custom category cards
            itemsIndexed(
                items = categories,
                key = { _, category -> "category_${category.id}" }
            ) { index, category ->
                val count = categoryCounts[category.id] ?: 0

                CategoryItemCard(
                    iconName = category.iconName,
                    name = category.name,
                    apkCount = count,
                    isSystem = false,
                    onClick = { onCategoryClick(category.id, category.name) },
                    onRename = { onRenameCategoryClick(category) },
                    onChangeIcon = { onChangeIconClick(category) },
                    onDelete = { onDeleteCategoryClick(category) },
                    shape = ExpressiveShapes.groupedCardShape(2 + index, totalCategoryItems)
                )
            }

            // Bottom Item: "+ Create Category" pill button (containerColor = buttonPrimary, 50% radius)
            item(key = "create_category_button_item") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onCreateCategoryClick,
                        shape = ExpressiveShapes.standaloneButton,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ExpressiveTheme.colors.buttonPrimary,
                            contentColor = ExpressiveTheme.colors.onButtonPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("create_category_pill_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Create Category",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItemCard(
    iconName: String,
    name: String,
    apkCount: Int,
    isSystem: Boolean,
    onClick: () -> Unit,
    onRename: (() -> Unit)? = null,
    onChangeIcon: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    shape: Shape = ExpressiveShapes.singleCard,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ExpressiveTheme.colors.cardSurface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("category_card_${name.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left: Category icon rendered via SVG loader with dynamic theme color
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(ExpressiveShapes.standaloneButton)
                    .background(ExpressiveTheme.colors.nestedElementSurface),
                contentAlignment = Alignment.Center
            ) {
                CategoryIconView(
                    iconName = iconName,
                    tint = ExpressiveTheme.colors.buttonPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Center: Category name (bold)
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpressiveTheme.colors.textPrimary
                ),
                modifier = Modifier.weight(1f)
            )

            // Right: APK count badge (e.g. "42 APKs")
            Box(
                modifier = Modifier
                    .clip(ExpressiveShapes.standaloneButton)
                    .background(ExpressiveTheme.colors.nestedElementSurface)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$apkCount APKs",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ExpressiveTheme.colors.textSecondary
                    )
                )
            }

            // Contextual action menu for custom categories
            if (!isSystem) {
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Category options",
                            tint = ExpressiveTheme.colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(ExpressiveTheme.colors.cardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onRename?.invoke()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Change Icon") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onChangeIcon?.invoke()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Delete",
                                    color = ExpressiveTheme.colors.errorColor
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = ExpressiveTheme.colors.errorColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete?.invoke()
                            }
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ApkVaultItem
import com.example.ui.components.ApkVaultCard
import com.example.ui.components.StoragePermissionBanner
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

import com.example.util.ApkScanner

@Composable
fun ApkVaultScreen(
    categoryTitle: String,
    items: List<ApkVaultItem>,
    isScanning: Boolean,
    hasStoragePermission: Boolean,
    searchQuery: String,
    onBackClick: () -> Unit,
    onRequestPermission: () -> Unit,
    onRescan: () -> Unit,
    onInstall: (ApkVaultItem) -> Unit,
    onDelete: (ApkVaultItem) -> Unit,
    onAssignCategory: (ApkVaultItem) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToExtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExpressiveTheme.colors.appBackground)
    ) {
        val topCardShape = ExpressiveShapes.groupedCardShape(0, 2)
        // Pinned Top Header Card with Back Button, Category Title, and Actions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp)
                .clip(topCardShape)
                .background(ExpressiveTheme.colors.cardSurface)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("vault_screen_header")
        ) {
            if (isSearchActive) {
                // Search Input View inside Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            onSearchQueryChange("")
                            isSearchActive = false
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close search",
                            tint = ExpressiveTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input"),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ExpressiveTheme.colors.textPrimary
                        ),
                        cursorBrush = SolidColor(ExpressiveTheme.colors.buttonPrimary),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search in $categoryTitle...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = ExpressiveTheme.colors.textMuted
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = ExpressiveTheme.colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // Category Title & Navigation Header View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button to CategoriesScreen
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("back_to_categories_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Categories",
                            tint = ExpressiveTheme.colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = categoryTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ExpressiveTheme.colors.textPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                        onClick = { isSearchActive = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_vault_search")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search category",
                            tint = ExpressiveTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Rescan Button
                    IconButton(
                        onClick = onRescan,
                        enabled = !isScanning,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("rescan_category_vault")
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
                                contentDescription = "Rescan",
                                tint = ExpressiveTheme.colors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Summary Card for Category (if items exist)
        if (items.isNotEmpty()) {
            val totalSizeBytes = remember(items) { items.sumOf { it.sizeBytes } }
            val formattedSize = remember(totalSizeBytes) { ApkScanner.formatFileSize(totalSizeBytes) }
            val updatesCount = remember(items) { items.count { it.isUpdate } }
            val summaryCardShape = ExpressiveShapes.groupedCardShape(1, 2)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 4.dp)
                    .clip(summaryCardShape)
                    .background(ExpressiveTheme.colors.cardSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("category_summary_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Storage Size: Icon + text rendered directly without background box
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
                            text = formattedSize,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ExpressiveTheme.colors.textSecondary
                            )
                        )
                    }

                    if (updatesCount > 0) {
                        Text(
                            text = "$updatesCount Updates Available",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                }
            }
        }

        // Strictly Vertical LazyColumn (NO horizontal scrolling)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("apk_vault_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Spacing between cards is 4dp
        ) {
            if (!hasStoragePermission) {
                item(key = "permission_banner") {
                    StoragePermissionBanner(
                        onRequestPermission = onRequestPermission,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            if (isScanning && items.isEmpty()) {
                item(key = "scanning_indicator") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(ExpressiveShapes.singleCard)
                            .background(ExpressiveTheme.colors.cardSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = ExpressiveTheme.colors.buttonPrimary,
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "Scanning device storage for .apk files...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = ExpressiveTheme.colors.textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            } else if (items.isEmpty()) {
                item(key = "empty_vault") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ExpressiveShapes.singleCard)
                            .background(ExpressiveTheme.colors.cardSurface)
                            .padding(24.dp)
                            .testTag("empty_vault_card"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(ExpressiveShapes.standaloneButton)
                                    .background(ExpressiveTheme.colors.nestedElementSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = null,
                                    tint = ExpressiveTheme.colors.buttonPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No Matching APKs" else "No APKs in '$categoryTitle'",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ExpressiveTheme.colors.textPrimary
                                    )
                                )
                                Text(
                                    text = if (searchQuery.isNotBlank()) {
                                        "No APKs match '$searchQuery' in this category."
                                    } else {
                                        "You can assign APKs to this category from 'All APKs' or extract apps directly from your device."
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ExpressiveTheme.colors.textSecondary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = onBackClick,
                                    shape = ExpressiveShapes.standaloneButton,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ExpressiveTheme.colors.buttonTertiarySubtle,
                                        contentColor = ExpressiveTheme.colors.onButtonTertiarySubtle
                                    ),
                                    modifier = Modifier.height(42.dp)
                                ) {
                                    Text("Categories")
                                }

                                Button(
                                    onClick = onNavigateToExtract,
                                    shape = ExpressiveShapes.standaloneButton,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ExpressiveTheme.colors.buttonPrimary,
                                        contentColor = ExpressiveTheme.colors.onButtonPrimary
                                    ),
                                    modifier = Modifier.height(42.dp)
                                ) {
                                    Text("Extract Apps")
                                }
                            }
                        }
                    }
                }
            } else {
                // Grouped APK Cards
                itemsIndexed(
                    items = items,
                    key = { _, item -> item.file.absolutePath }
                ) { index, item ->
                    ApkVaultCard(
                        item = item,
                        index = index,
                        total = items.size,
                        onInstall = onInstall,
                        onDelete = onDelete,
                        onAssignCategory = onAssignCategory
                    )
                }
            }
        }
    }
}

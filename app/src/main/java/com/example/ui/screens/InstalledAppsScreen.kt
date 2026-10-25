package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import com.example.model.InstalledAppItem
import com.example.ui.components.InstalledAppCard
import com.example.ui.components.NestedMetadataBadge
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@Composable
fun InstalledAppsScreen(
    apps: List<InstalledAppItem>,
    isLoading: Boolean,
    showSystemApps: Boolean,
    searchQuery: String,
    extractingPackage: String?,
    extractedInSession: Set<String>,
    onBackClick: () -> Unit,
    onToggleSystemApps: (Boolean) -> Unit,
    onExtract: (InstalledAppItem) -> Unit,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExpressiveTheme.colors.appBackground)
    ) {
        // Pinned Top Header Card with Back Button and Actions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(ExpressiveShapes.singleCard)
                .background(ExpressiveTheme.colors.cardSurface)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("installed_screen_header")
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
                            .testTag("installed_search_input"),
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
                                    text = "Search installed applications...",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("installed_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to categories",
                            tint = ExpressiveTheme.colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp)
                    ) {
                        Text(
                            text = "Extract Installed Apps",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ExpressiveTheme.colors.textPrimary,
                                letterSpacing = (-0.2).sp
                            )
                        )
                        Text(
                            text = "${apps.size} packages available",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ExpressiveTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { isSearchActive = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("installed_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search apps",
                            tint = ExpressiveTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("installed_refresh_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ExpressiveTheme.colors.buttonPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload installed apps",
                                tint = ExpressiveTheme.colors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("installed_apps_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Spacing between cards is 4dp
        ) {
            // Filter Controls Header Card
            item(key = "installed_controls_header") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ExpressiveShapes.singleCard)
                        .background(ExpressiveTheme.colors.cardSurface)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("system_apps_toggle_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = ExpressiveTheme.colors.buttonPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Show System Apps",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ExpressiveTheme.colors.textPrimary
                                )
                            )
                            Text(
                                text = "${apps.size} apps available to extract",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ExpressiveTheme.colors.textSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = showSystemApps,
                        onCheckedChange = onToggleSystemApps,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ExpressiveTheme.colors.onButtonPrimary,
                            checkedTrackColor = ExpressiveTheme.colors.buttonPrimary,
                            uncheckedThumbColor = ExpressiveTheme.colors.textSecondary,
                            uncheckedTrackColor = ExpressiveTheme.colors.nestedElementSurface
                        ),
                        modifier = Modifier.testTag("system_apps_switch")
                    )
                }
            }
        }

        if (isLoading && apps.isEmpty()) {
            item(key = "loading_apps") {
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
                            text = "Reading installed packages and metadata...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ExpressiveTheme.colors.textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        } else if (apps.isEmpty()) {
            item(key = "empty_apps") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ExpressiveShapes.singleCard)
                        .background(ExpressiveTheme.colors.cardSurface)
                        .padding(24.dp)
                        .testTag("empty_installed_card"),
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
                                imageVector = Icons.Default.Apps,
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
                                text = if (searchQuery.isNotBlank()) "No Matching Applications" else "No Installed Apps Found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ExpressiveTheme.colors.textPrimary
                                )
                            )
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "No installed apps match '$searchQuery'."
                                } else {
                                    "No user applications found. Toggle 'Show System Apps' above to browse and extract pre-installed apps."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ExpressiveTheme.colors.textSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Button(
                            onClick = onRefresh,
                            shape = ExpressiveShapes.standaloneButton,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ExpressiveTheme.colors.buttonPrimary,
                                contentColor = ExpressiveTheme.colors.onButtonPrimary
                            ),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Reload Installed Apps")
                            }
                        }
                    }
                }
            }
        } else {
            // Grouped Installed App Cards
            itemsIndexed(
                items = apps,
                key = { _, app -> app.packageName }
            ) { index, app ->
                InstalledAppCard(
                    app = app,
                    index = index,
                    total = apps.size,
                    isExtracting = extractingPackage == app.packageName,
                    isExtractedInSession = extractedInSession.contains(app.packageName),
                    onExtract = onExtract
                )
            }
        }
    }
}
}


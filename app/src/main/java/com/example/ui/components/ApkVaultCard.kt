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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LabelOff
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ApkVaultItem
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@Composable
fun ApkVaultCard(
    item: ApkVaultItem,
    index: Int,
    total: Int,
    onInstall: (ApkVaultItem) -> Unit,
    onDelete: (ApkVaultItem) -> Unit,
    onAssignCategory: (ApkVaultItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = ExpressiveShapes.groupedCardShape(index, total)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(ExpressiveTheme.colors.cardSurface)
            .padding(16.dp)
            .testTag("apk_card_${item.packageName}")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Icon, Title, Package Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppIconImage(
                    drawable = item.iconDrawable,
                    contentDescription = item.label,
                    size = 52.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExpressiveTheme.colors.textPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = item.packageName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ExpressiveTheme.colors.textSecondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Metadata Chips with Category Assignment Pill (Nested inside card -> must use nestedElementSurface)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Assignment Pill Button (percent = 50, background = nestedElementSurface)
                Box(
                    modifier = Modifier
                        .clip(ExpressiveShapes.standaloneButton)
                        .background(ExpressiveTheme.colors.nestedElementSurface)
                        .clickable { onAssignCategory(item) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("assign_category_button_${item.packageName}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (item.categoryName != null) Icons.Default.Label else Icons.Default.LabelOff,
                            contentDescription = "Assign Category",
                            tint = if (item.categoryName != null) ExpressiveTheme.colors.buttonPrimary else ExpressiveTheme.colors.textSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = item.categoryName ?: "Uncategorized",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (item.categoryName != null) ExpressiveTheme.colors.textPrimary else ExpressiveTheme.colors.textSecondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Version Chip
                NestedMetadataBadge(
                    text = "v${item.versionName}",
                    modifier = Modifier.weight(1f, fill = false)
                )

                // File Size Chip
                NestedMetadataBadge(
                    icon = Icons.Default.SdStorage,
                    text = item.formattedSize
                )

                // Date Chip
                NestedMetadataBadge(
                    icon = Icons.Default.CalendarToday,
                    text = item.formattedDate.substringBefore(" ")
                )
            }

            // Installation status strip (Nested inside card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ExpressiveTheme.colors.nestedElementSurface)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when {
                        item.isUpdate -> {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Installed (v${item.installedVersionName}) • Update Available",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                        }
                        item.isInstalled -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Installed (v${item.installedVersionName})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(ExpressiveTheme.colors.textMuted)
                            )
                            Text(
                                text = "Not installed on this device",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ExpressiveTheme.colors.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Card Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary / Delete Button (Low-saturation Monet Tertiary)
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(ExpressiveShapes.standaloneButton)
                        .background(ExpressiveTheme.colors.buttonTertiarySubtle)
                        .clickable { onDelete(item) }
                        .padding(horizontal = 16.dp)
                        .testTag("delete_button_${item.packageName}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete APK",
                            tint = ExpressiveTheme.colors.onButtonTertiarySubtle,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = ExpressiveTheme.colors.onButtonTertiarySubtle,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Primary Install Button (Monet Primary)
                Button(
                    onClick = { onInstall(item) },
                    shape = ExpressiveShapes.standaloneButton,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpressiveTheme.colors.buttonPrimary,
                        contentColor = ExpressiveTheme.colors.onButtonPrimary
                    ),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("install_button_${item.packageName}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isUpdate) Icons.Default.SystemUpdate else Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (item.isUpdate) "Update APK" else if (item.isInstalled) "Reinstall" else "Install APK",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NestedMetadataBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ExpressiveTheme.colors.nestedElementSurface)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = ExpressiveTheme.colors.textSecondary
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ExpressiveTheme.colors.textSecondary,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

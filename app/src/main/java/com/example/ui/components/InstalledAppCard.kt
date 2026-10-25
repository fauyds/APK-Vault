package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.model.InstalledAppItem
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@Composable
fun InstalledAppCard(
    app: InstalledAppItem,
    index: Int,
    total: Int,
    isExtracting: Boolean,
    isExtractedInSession: Boolean,
    onExtract: (InstalledAppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = ExpressiveShapes.groupedCardShape(index, total)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(ExpressiveTheme.colors.cardSurface)
            .padding(16.dp)
            .testTag("installed_app_${app.packageName}")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Identity: Icon, Label, Package
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppIconImage(
                    drawable = app.iconDrawable,
                    contentDescription = app.label,
                    size = 52.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExpressiveTheme.colors.textPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ExpressiveTheme.colors.textSecondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Metadata Chips (Nested inside card -> must use nestedElementSurface)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NestedMetadataBadge(
                    text = "v${app.versionName}",
                    modifier = Modifier.weight(1f, fill = false)
                )

                NestedMetadataBadge(
                    icon = Icons.Default.SdStorage,
                    text = app.formattedSize
                )

                if (app.isSystemApp) {
                    NestedMetadataBadge(
                        icon = Icons.Default.Layers,
                        text = "System App"
                    )
                }
            }

            // Extract Action Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isExtractedInSession) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Extracted to Vault",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Extract Button: Pill shape
                Button(
                    onClick = { onExtract(app) },
                    enabled = !isExtracting,
                    shape = ExpressiveShapes.standaloneButton,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExtractedInSession) {
                            ExpressiveTheme.colors.buttonTertiarySubtle
                        } else {
                            ExpressiveTheme.colors.buttonPrimary
                        },
                        contentColor = if (isExtractedInSession) {
                            ExpressiveTheme.colors.onButtonTertiarySubtle
                        } else {
                            ExpressiveTheme.colors.onButtonPrimary
                        }
                    ),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("extract_button_${app.packageName}")
                ) {
                    if (isExtracting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ExpressiveTheme.colors.onButtonPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isExtractedInSession) "Extract Again" else "Extract APK",
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
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ApkVaultItem
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@Composable
fun DeleteConfirmationDialog(
    item: ApkVaultItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
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
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = ExpressiveTheme.colors.errorColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "Delete APK Package?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpressiveTheme.colors.textPrimary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Are you sure you want to delete '${item.label}' from device storage? This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ExpressiveTheme.colors.textSecondary
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ExpressiveShapes.groupedCardShape(0, 1))
                        .background(ExpressiveTheme.colors.nestedElementSurface)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = item.file.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ExpressiveTheme.colors.textPrimary
                            )
                        )
                        Text(
                            text = item.file.parent ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ExpressiveTheme.colors.textMuted
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = ExpressiveShapes.standaloneButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExpressiveTheme.colors.errorColor,
                    contentColor = ExpressiveTheme.colors.onButtonPrimary
                ),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("Delete File", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = ExpressiveShapes.standaloneButton,
                modifier = Modifier.testTag("cancel_delete_button")
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

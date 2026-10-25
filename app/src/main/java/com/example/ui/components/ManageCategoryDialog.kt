package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.CategoryEntity
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@Composable
fun ManageCategoryDialog(
    category: CategoryEntity,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    var editedName by remember { mutableStateOf(category.name) }
    var isConfirmingDelete by remember { mutableStateOf(false) }

    if (isConfirmingDelete) {
        AlertDialog(
            onDismissRequest = { isConfirmingDelete = false },
            shape = ExpressiveShapes.singleCard,
            containerColor = ExpressiveTheme.colors.cardSurface,
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
                    text = "Are you sure you want to delete '${category.name}'? APKs in this category will be moved to 'Uncategorized'.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ExpressiveTheme.colors.textSecondary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isConfirmingDelete = false
                        onDelete()
                    },
                    shape = ExpressiveShapes.standaloneButton,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpressiveTheme.colors.errorColor,
                        contentColor = ExpressiveTheme.colors.onButtonPrimary
                    )
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isConfirmingDelete = false },
                    shape = ExpressiveShapes.standaloneButton
                ) {
                    Text("Cancel", color = ExpressiveTheme.colors.textSecondary)
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = ExpressiveShapes.singleCard,
            containerColor = ExpressiveTheme.colors.cardSurface,
            title = {
                Text(
                    text = "Manage Category",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ExpressiveTheme.colors.textPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Edit name or remove category:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ExpressiveTheme.colors.textSecondary
                        )
                    )

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        singleLine = true,
                        label = { Text("Category name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ExpressiveTheme.colors.buttonPrimary,
                            unfocusedBorderColor = ExpressiveTheme.colors.nestedElementSurface,
                            focusedTextColor = ExpressiveTheme.colors.textPrimary,
                            unfocusedTextColor = ExpressiveTheme.colors.textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { isConfirmingDelete = true },
                        shape = ExpressiveShapes.standaloneButton,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ExpressiveTheme.colors.buttonTertiarySubtle,
                            contentColor = ExpressiveTheme.colors.errorColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = ExpressiveTheme.colors.errorColor
                            )
                            Text("Delete Category", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = editedName.trim()
                        if (trimmed.isNotEmpty()) {
                            onRename(trimmed)
                        }
                    },
                    enabled = editedName.trim().isNotEmpty() && editedName.trim() != category.name,
                    shape = ExpressiveShapes.standaloneButton,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpressiveTheme.colors.buttonPrimary,
                        contentColor = ExpressiveTheme.colors.onButtonPrimary
                    )
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    shape = ExpressiveShapes.standaloneButton
                ) {
                    Text("Cancel", color = ExpressiveTheme.colors.textSecondary)
                }
            }
        )
    }
}

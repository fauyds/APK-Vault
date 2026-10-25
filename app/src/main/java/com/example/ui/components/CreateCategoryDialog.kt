package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    val isBlank = categoryName.trim().isEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ExpressiveShapes.singleCard,
        containerColor = ExpressiveTheme.colors.cardSurface,
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter a name for the new APK category (e.g., Tools, Games, System):",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ExpressiveTheme.colors.textSecondary
                    )
                )

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    singleLine = true,
                    placeholder = { Text("Category name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ExpressiveTheme.colors.buttonPrimary,
                        unfocusedBorderColor = ExpressiveTheme.colors.nestedElementSurface,
                        focusedTextColor = ExpressiveTheme.colors.textPrimary,
                        unfocusedTextColor = ExpressiveTheme.colors.textPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_category_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isBlank) {
                        onConfirm(categoryName.trim())
                    }
                },
                enabled = !isBlank,
                shape = ExpressiveShapes.standaloneButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExpressiveTheme.colors.buttonPrimary,
                    contentColor = ExpressiveTheme.colors.onButtonPrimary
                ),
                modifier = Modifier.testTag("confirm_create_category_button")
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

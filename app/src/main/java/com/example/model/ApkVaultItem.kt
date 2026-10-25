package com.example.model

import android.graphics.drawable.Drawable
import java.io.File

data class ApkVaultItem(
    val file: File,
    val label: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val sizeBytes: Long,
    val formattedSize: String,
    val lastModified: Long,
    val formattedDate: String,
    val isInstalled: Boolean,
    val installedVersionName: String? = null,
    val installedVersionCode: Long? = null,
    val isUpdate: Boolean = false,
    val iconDrawable: Drawable? = null,
    val categoryId: Long? = null,
    val categoryName: String? = null
)

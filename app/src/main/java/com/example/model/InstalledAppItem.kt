package com.example.model

import android.graphics.drawable.Drawable

data class InstalledAppItem(
    val label: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val apkPath: String,
    val sizeBytes: Long,
    val formattedSize: String,
    val isSystemApp: Boolean,
    val iconDrawable: Drawable? = null
)

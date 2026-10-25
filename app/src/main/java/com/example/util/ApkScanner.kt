package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import com.example.model.ApkVaultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ApkScanner {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    suspend fun scanStorageForApks(context: Context): List<ApkVaultItem> = withContext(Dispatchers.IO) {
        val discoveredFiles = mutableSetOf<File>()

        val rootDir = Environment.getExternalStorageDirectory()
        if (rootDir != null && rootDir.exists() && rootDir.canRead()) {
            scanDirectoryRecursively(rootDir, discoveredFiles, depth = 0, maxDepth = 5)
        }

        // Also ensure common locations are scanned directly
        val commonDirs = listOfNotNull(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "APK_Archive"),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            context.getExternalFilesDir(null),
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        )

        for (dir in commonDirs) {
            if (dir.exists() && dir.canRead()) {
                scanDirectoryRecursively(dir, discoveredFiles, depth = 0, maxDepth = 4)
            }
        }

        val pm = context.packageManager
        val items = mutableListOf<ApkVaultItem>()

        for (file in discoveredFiles) {
            try {
                val item = parseApkFile(file, pm)
                if (item != null) {
                    items.add(item)
                }
            } catch (_: Exception) {
                // Ignore unreadable or corrupted files
            }
        }

        // Sort by last modified descending (newest first)
        items.sortedByDescending { it.lastModified }
    }

    private fun scanDirectoryRecursively(
        directory: File,
        results: MutableSet<File>,
        depth: Int,
        maxDepth: Int
    ) {
        if (depth > maxDepth) return
        val list = try {
            directory.listFiles()
        } catch (_: SecurityException) {
            null
        } ?: return

        for (file in list) {
            if (file.isDirectory) {
                val name = file.name
                // Skip hidden folders and protected Android data folders
                if (name.startsWith(".") ||
                    name.equals("Android", ignoreCase = true) && depth == 0
                ) {
                    continue
                }
                scanDirectoryRecursively(file, results, depth + 1, maxDepth)
            } else if (file.isFile && file.name.endsWith(".apk", ignoreCase = true)) {
                results.add(file)
            }
        }
    }

    private fun parseApkFile(file: File, pm: PackageManager): ApkVaultItem? {
        val path = file.absolutePath
        val pi = pm.getPackageArchiveInfo(path, PackageManager.GET_META_DATA)
        val label: String
        val packageName: String
        val versionName: String
        val versionCode: Long
        var icon: android.graphics.drawable.Drawable? = null

        if (pi != null) {
            packageName = pi.packageName
            versionName = pi.versionName ?: "Unknown"
            versionCode = PackageInfoCompat.getLongVersionCode(pi)
            val appInfo = pi.applicationInfo
            if (appInfo != null) {
                appInfo.sourceDir = path
                appInfo.publicSourceDir = path
                label = try {
                    appInfo.loadLabel(pm).toString().takeIf { it.isNotBlank() }
                        ?: file.nameWithoutExtension
                } catch (_: Exception) {
                    file.nameWithoutExtension
                }
                icon = try {
                    appInfo.loadIcon(pm)
                } catch (_: Exception) {
                    null
                }
            } else {
                label = file.nameWithoutExtension
            }
        } else {
            // Unparseable APK fallback
            packageName = "unknown.package"
            versionName = "N/A"
            versionCode = 0L
            label = file.nameWithoutExtension
        }

        var isInstalled = false
        var installedVersionName: String? = null
        var installedVersionCode: Long? = null
        var isUpdate = false

        if (packageName != "unknown.package") {
            try {
                val installedPi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    pm.getPackageInfo(packageName, 0)
                }
                isInstalled = true
                installedVersionName = installedPi.versionName
                val currentInstalledCode = PackageInfoCompat.getLongVersionCode(installedPi)
                installedVersionCode = currentInstalledCode
                if (versionCode > currentInstalledCode) {
                    isUpdate = true
                }
            } catch (_: PackageManager.NameNotFoundException) {
                isInstalled = false
            } catch (_: Exception) {
                isInstalled = false
            }
        }

        val sizeBytes = file.length()
        val formattedSize = formatFileSize(sizeBytes)
        val lastModified = file.lastModified()
        val formattedDate = dateFormat.format(Date(lastModified))

        return ApkVaultItem(
            file = file,
            label = label,
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,
            sizeBytes = sizeBytes,
            formattedSize = formattedSize,
            lastModified = lastModified,
            formattedDate = formattedDate,
            isInstalled = isInstalled,
            installedVersionName = installedVersionName,
            installedVersionCode = installedVersionCode,
            isUpdate = isUpdate,
            iconDrawable = icon
        )
    }

    fun formatFileSize(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1.0) {
            String.format(Locale.US, "%.1f MB", mb)
        } else {
            val kb = bytes / 1024.0
            String.format(Locale.US, "%.1f KB", kb)
        }
    }

    fun installApk(context: Context, file: File): Result<Unit> {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteApk(file: File): Boolean {
        return try {
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }
}

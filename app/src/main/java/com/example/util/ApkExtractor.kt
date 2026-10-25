package com.example.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.pm.PackageInfoCompat
import com.example.model.InstalledAppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ApkExtractor {

    suspend fun getInstalledApps(
        context: Context,
        includeSystemApps: Boolean
    ): List<InstalledAppItem> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledPackages(PackageManager.GET_META_DATA)
        }

        val appList = mutableListOf<InstalledAppItem>()

        for (pi in packages) {
            val appInfo = pi.applicationInfo ?: continue
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            if (!includeSystemApps && isSystem) {
                continue
            }

            // Exclude our own app from extraction list to keep it focused
            if (pi.packageName == context.packageName) {
                continue
            }

            val apkPath = appInfo.publicSourceDir ?: appInfo.sourceDir ?: continue
            val apkFile = File(apkPath)
            if (!apkFile.exists() || !apkFile.canRead()) {
                continue
            }

            val label = try {
                appInfo.loadLabel(pm).toString().takeIf { it.isNotBlank() } ?: pi.packageName
            } catch (_: Exception) {
                pi.packageName
            }

            val icon = try {
                appInfo.loadIcon(pm)
            } catch (_: Exception) {
                null
            }

            val versionName = pi.versionName ?: "1.0"
            val versionCode = PackageInfoCompat.getLongVersionCode(pi)
            val sizeBytes = apkFile.length()
            val formattedSize = ApkScanner.formatFileSize(sizeBytes)

            appList.add(
                InstalledAppItem(
                    label = label,
                    packageName = pi.packageName,
                    versionName = versionName,
                    versionCode = versionCode,
                    apkPath = apkPath,
                    sizeBytes = sizeBytes,
                    formattedSize = formattedSize,
                    isSystemApp = isSystem,
                    iconDrawable = icon
                )
            )
        }

        appList.sortedBy { it.label.lowercase() }
    }

    suspend fun extractApk(
        context: Context,
        app: InstalledAppItem
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val sourcePath = app.apkPath
            val sourceFile = File(sourcePath)
            if (!sourceFile.exists() || !sourceFile.canRead()) {
                return@withContext Result.failure(Exception("Cannot read APK from $sourcePath"))
            }

            // Target directory: Downloads / APK_Archive
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var targetDir = File(downloadsDir, "APK_Archive")
            if (!targetDir.exists()) {
                val created = targetDir.mkdirs()
                if (!created && !targetDir.exists()) {
                    val fallback = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    targetDir = File(fallback, "APK_Archive")
                    targetDir.mkdirs()
                }
            }

            val cleanAppName = sanitizeFileName(app.label)
            val cleanVersion = sanitizeFileName(app.versionName)
            val targetFileName = "${cleanAppName}_$cleanVersion.apk"
            val targetFile = File(targetDir, targetFileName)

            FileInputStream(sourceFile).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output, bufferSize = 64 * 1024)
                }
            }

            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
            .ifEmpty { "app" }
    }
}

package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.request.ImageRequest

object SvgImageLoader {
    @Volatile
    private var instance: ImageLoader? = null

    fun get(context: Context): ImageLoader {
        return instance ?: synchronized(this) {
            instance ?: ImageLoader.Builder(context.applicationContext)
                .components {
                    add(SvgDecoder.Factory())
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.applicationContext.cacheDir.resolve("svg_icon_cache"))
                        .maxSizeBytes(25 * 1024 * 1024) // 25 MB disk cache for SVGs
                        .build()
                }
                .crossfade(true)
                .build().also { instance = it }
        }
    }
}

object CategoryIconHelper {

    // Master curated list of popular Google Material Symbols
    val masterIconList: List<String> = listOf(
        "folder",
        "sports_esports",
        "terminal",
        "settings",
        "build",
        "palette",
        "shield",
        "smart_toy",
        "extension",
        "music_note",
        "favorite",
        "circle",
        "square",
        "code",
        "storage",
        "cloud",
        "android",
        "gamepad",
        "movie",
        "dashboard",
        "lock",
        "star",
        "file_download",
        "archive",
        "bug_report",
        "security",
        "phone_android",
        "tune",
        "widgets",
        "style",
        "grid_view",
        "analytics",
        "cached",
        "speed",
        "bolt",
        "rocket_launch",
        "work",
        "camera_alt",
        "shopping_bag",
        "travel_explore",
        "home",
        "info",
        "workspace_premium",
        "history",
        "sd_card",
        "apps",
        "inventory_2",
        "folder_open"
    )

    val availableIcons: List<Pair<String, ImageVector>> = listOf(
        "folder" to Icons.Default.Folder,
        "sports_esports" to Icons.Default.SportsEsports,
        "terminal" to Icons.Default.Terminal,
        "settings" to Icons.Default.Settings,
        "build" to Icons.Default.Build,
        "palette" to Icons.Default.Palette,
        "shield" to Icons.Default.Shield,
        "smart_toy" to Icons.Default.SmartToy,
        "music_note" to Icons.Default.MusicNote,
        "favorite" to Icons.Default.Favorite,
        "code" to Icons.Default.Code,
        "movie" to Icons.Default.Movie,
        "work" to Icons.Default.Work,
        "security" to Icons.Default.Security,
        "star" to Icons.Default.Star,
        "apps" to Icons.Default.Apps,
        "file_download" to Icons.Default.FileDownload,
        "archive" to Icons.Default.Archive
    )

    fun toSymbolName(iconName: String): String {
        val clean = iconName.trim()
        if (clean.isBlank()) return "folder"
        return when (clean) {
            "Folder" -> "folder"
            "SportsEsports", "Games" -> "sports_esports"
            "Build", "Tools" -> "build"
            "Movie", "Media" -> "movie"
            "Work", "Productivity" -> "work"
            "Security" -> "security"
            "Star" -> "star"
            "Apps" -> "apps"
            "FileDownload" -> "file_download"
            "Archive" -> "archive"
            "FolderOpen" -> "folder_open"
            else -> clean.lowercase().replace(" ", "_")
        }
    }

    fun getMaterialSymbolSvgUrl(iconName: String): String {
        val cleanName = toSymbolName(iconName)
        return "https://raw.githubusercontent.com/google/material-design-icons/master/symbols/web/$cleanName/materialsymbolsrounded/${cleanName}_24px.svg"
    }

    fun getIcon(iconName: String): ImageVector {
        val symbolName = toSymbolName(iconName)
        return when (symbolName) {
            "sports_esports", "gamepad" -> Icons.Default.SportsEsports
            "terminal" -> Icons.Default.Terminal
            "settings", "tune" -> Icons.Default.Settings
            "build" -> Icons.Default.Build
            "palette", "style" -> Icons.Default.Palette
            "shield", "security", "lock" -> Icons.Default.Shield
            "smart_toy", "android", "bug_report" -> Icons.Default.SmartToy
            "music_note" -> Icons.Default.MusicNote
            "favorite" -> Icons.Default.Favorite
            "code" -> Icons.Default.Code
            "movie" -> Icons.Default.Movie
            "work" -> Icons.Default.Work
            "star" -> Icons.Default.Star
            "apps", "widgets", "grid_view" -> Icons.Default.Apps
            "file_download" -> Icons.Default.FileDownload
            "archive" -> Icons.Default.Archive
            "folder_open" -> Icons.Default.FolderOpen
            else -> Icons.Default.Folder
        }
    }
}

@Composable
fun CategoryIconView(
    iconName: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val url = remember(iconName) { CategoryIconHelper.getMaterialSymbolSvgUrl(iconName) }
    val fallbackVector = remember(iconName) { CategoryIconHelper.getIcon(iconName) }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        imageLoader = SvgImageLoader.get(context),
        contentDescription = iconName,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    color = tint,
                    strokeWidth = 1.5.dp
                )
            }
        },
        error = {
            Icon(
                imageVector = fallbackVector,
                contentDescription = iconName,
                tint = tint,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

package com.example.data.repository

import android.content.Context
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class IconCatalogRepository(private val context: Context) {

    private val cacheFile = File(context.filesDir, "material_icons_codepoints.txt")

    suspend fun getIconCatalog(): List<String> = withContext(Dispatchers.IO) {
        // 1. Check if cached file exists and has content
        if (cacheFile.exists() && cacheFile.length() > 0) {
            try {
                val cachedIcons = cacheFile.readLines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                if (cachedIcons.size >= 10) {
                    return@withContext cachedIcons
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Fetch from Google Fonts official codepoints master file
        val remoteIcons = fetchFromNetwork()
        if (!remoteIcons.isNullOrEmpty()) {
            try {
                cacheFile.writeText(remoteIcons.joinToString("\n"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext remoteIcons
        }

        // 3. Fall back to res/raw/material_icons_fallback.txt resource
        val fallbackIcons = loadFromFallbackResource()
        if (fallbackIcons.isNotEmpty()) {
            return@withContext fallbackIcons
        }

        // 4. Emergency hardcoded minimal safety list
        return@withContext listOf(
            "folder", "sports_esports", "terminal", "settings", "build",
            "palette", "shield", "smart_toy", "extension", "music_note", "favorite"
        )
    }

    private fun fetchFromNetwork(): List<String>? {
        return try {
            val url = URL("https://raw.githubusercontent.com/google/material-design-icons/master/font/MaterialIcons-Regular.codepoints")
            val connection = url.openConnection()
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val text = connection.getInputStream().bufferedReader().readText()
            val parsed = text.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .map { it.substringBefore(' ').trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .toList()
            if (parsed.size > 50) parsed else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadFromFallbackResource(): List<String> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.material_icons_fallback)
            inputStream.bufferedReader().useLines { lines ->
                lines.map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
                    .map { it.substringBefore(' ').trim() }
                    .filter { it.isNotEmpty() }
                    .distinct()
                    .toList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ApkVaultDatabase
import com.example.data.entity.ApkCategoryMappingEntity
import com.example.data.entity.CategoryEntity
import com.example.ui.theme.ExpressiveShapes
import com.example.util.ApkScanner
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("APK Vault", appName)
    }

    @Test
    fun `format file size tests`() {
        assertEquals("500.0 KB", ApkScanner.formatFileSize(500 * 1024L))
        assertEquals("15.5 MB", ApkScanner.formatFileSize((15.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun `expressive shapes tests`() {
        assertNotNull(ExpressiveShapes.standaloneButton)
        assertNotNull(ExpressiveShapes.singleCard)
        assertNotNull(ExpressiveShapes.groupedCardShape(0, 3))
        assertNotNull(ExpressiveShapes.selectorSegmentShape(0, 2, isSelected = true))
    }

    @Test
    fun `test category database insertion and mapping`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, ApkVaultDatabase::class.java).build()
        val dao = db.categoryDao()
        val catId = dao.insertCategory(CategoryEntity(name = "Tools"))
        assertTrue(catId > 0)
        dao.assignCategory(ApkCategoryMappingEntity(packageName = "com.tools.sample", categoryId = catId))
        val mappedCatId = dao.getCategoryIdForPackage("com.tools.sample")
        assertEquals(catId, mappedCatId)
        db.close()
    }
}

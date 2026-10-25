package com.example.data.repository

import com.example.data.dao.CategoryDao
import com.example.data.entity.ApkCategoryMappingEntity
import com.example.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class CategoryRepository(private val categoryDao: CategoryDao) {

    val categories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val mappings: Flow<List<ApkCategoryMappingEntity>> = categoryDao.getAllMappings()

    suspend fun addCategory(name: String, iconName: String = "Folder"): Long {
        return categoryDao.insertCategory(CategoryEntity(name = name.trim(), iconName = iconName))
    }

    suspend fun updateCategory(id: Long, newName: String, iconName: String) {
        categoryDao.updateCategory(CategoryEntity(id = id, name = newName.trim(), iconName = iconName))
    }

    suspend fun deleteCategory(id: Long) {
        // Deleting category moves APKs to "Uncategorized" by removing mappings
        categoryDao.deleteMappingsForCategory(id)
        categoryDao.deleteCategory(id)
    }

    suspend fun assignCategory(packageName: String, categoryId: Long?) {
        if (categoryId == null || categoryId <= 0) {
            categoryDao.removeMappingForPackage(packageName)
        } else {
            categoryDao.assignCategory(ApkCategoryMappingEntity(packageName = packageName, categoryId = categoryId))
        }
    }

    suspend fun exportBackupJson(): String {
        val catList = categoryDao.getAllCategoriesList()
        val mapList = categoryDao.getAllMappingsList()

        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "ApkVault")
        root.put("timestamp", System.currentTimeMillis())

        val categoriesArr = JSONArray()
        catList.forEach { cat ->
            val obj = JSONObject()
            obj.put("id", cat.id)
            obj.put("name", cat.name)
            obj.put("iconName", cat.iconName)
            obj.put("createdAt", cat.createdAt)
            categoriesArr.put(obj)
        }
        root.put("categories", categoriesArr)

        val mappingsArr = JSONArray()
        mapList.forEach { map ->
            val obj = JSONObject()
            obj.put("packageName", map.packageName)
            obj.put("categoryId", map.categoryId)
            mappingsArr.put(obj)
        }
        root.put("mappings", mappingsArr)

        return root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Result<Pair<Int, Int>> {
        return try {
            val root = JSONObject(jsonString)
            val categoriesArr = root.optJSONArray("categories") ?: JSONArray()
            val mappingsArr = root.optJSONArray("mappings") ?: JSONArray()

            val catList = mutableListOf<CategoryEntity>()
            for (i in 0 until categoriesArr.length()) {
                val obj = categoriesArr.getJSONObject(i)
                catList.add(
                    CategoryEntity(
                        id = obj.getLong("id"),
                        name = obj.getString("name"),
                        iconName = obj.optString("iconName", "folder"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val mapList = mutableListOf<ApkCategoryMappingEntity>()
            for (i in 0 until mappingsArr.length()) {
                val obj = mappingsArr.getJSONObject(i)
                mapList.add(
                    ApkCategoryMappingEntity(
                        packageName = obj.getString("packageName"),
                        categoryId = obj.getLong("categoryId")
                    )
                )
            }

            if (catList.isNotEmpty()) {
                categoryDao.insertOrUpdateCategories(catList)
            }
            if (mapList.isNotEmpty()) {
                categoryDao.insertOrUpdateMappings(mapList)
            }

            Result.success(Pair(catList.size, mapList.size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

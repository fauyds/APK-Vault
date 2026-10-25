package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ApkCategoryMappingEntity
import com.example.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Long)

    @Query("DELETE FROM apk_category_mappings WHERE categoryId = :categoryId")
    suspend fun deleteMappingsForCategory(categoryId: Long)

    @Query("SELECT * FROM apk_category_mappings")
    fun getAllMappings(): Flow<List<ApkCategoryMappingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun assignCategory(mapping: ApkCategoryMappingEntity)

    @Query("DELETE FROM apk_category_mappings WHERE packageName = :packageName")
    suspend fun removeMappingForPackage(packageName: String)

    @Query("SELECT categoryId FROM apk_category_mappings WHERE packageName = :packageName LIMIT 1")
    suspend fun getCategoryIdForPackage(packageName: String): Long?

    // Backup & Restore
    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllCategoriesList(): List<CategoryEntity>

    @Query("SELECT * FROM apk_category_mappings")
    suspend fun getAllMappingsList(): List<ApkCategoryMappingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMappings(mappings: List<ApkCategoryMappingEntity>)
}

package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CategoryDao
import com.example.data.entity.ApkCategoryMappingEntity
import com.example.data.entity.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CategoryEntity::class, ApkCategoryMappingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApkVaultDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: ApkVaultDatabase? = null

        fun getInstance(context: Context): ApkVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApkVaultDatabase::class.java,
                    "apk_vault.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).categoryDao()
                            dao.insertCategory(CategoryEntity(name = "Tools", iconName = "Build"))
                            dao.insertCategory(CategoryEntity(name = "Games", iconName = "SportsEsports"))
                            dao.insertCategory(CategoryEntity(name = "Media", iconName = "Movie"))
                            dao.insertCategory(CategoryEntity(name = "Productivity", iconName = "Work"))
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

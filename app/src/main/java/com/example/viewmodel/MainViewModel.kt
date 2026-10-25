package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ApkVaultDatabase
import com.example.data.entity.CategoryEntity
import com.example.data.repository.CategoryRepository
import com.example.model.ApkVaultItem
import com.example.model.InstalledAppItem
import com.example.ui.screens.CATEGORY_ID_ALL
import com.example.ui.screens.CATEGORY_ID_UNCATEGORIZED
import com.example.util.ApkExtractor
import com.example.util.ApkScanner
import com.example.util.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    CATEGORIES,    // Primary / Home start destination
    APK_LIST,      // Screen opened when a category is tapped
    INSTALLED_APPS // Installed apps extractor
}

data class MainUiState(
    val currentScreen: AppScreen = AppScreen.CATEGORIES,
    val selectedCategoryId: Long = CATEGORY_ID_ALL,
    val selectedCategoryName: String = "All APKs",
    val rawVaultItems: List<ApkVaultItem> = emptyList(),
    val vaultItems: List<ApkVaultItem> = emptyList(),
    val isScanningVault: Boolean = false,
    val installedApps: List<InstalledAppItem> = emptyList(),
    val isLoadingInstalled: Boolean = false,
    val searchQuery: String = "",
    val showSystemApps: Boolean = false,
    val hasStoragePermission: Boolean = false,
    val extractingPackage: String? = null,
    val extractedInSession: Set<String> = emptySet(),
    val itemPendingDelete: ApkVaultItem? = null,
    val userMessage: String? = null,

    // Categories
    val categories: List<CategoryEntity> = emptyList(),
    val categoryMappings: Map<String, Long> = emptyMap(),

    // Dialogs
    val showCreateCategoryDialog: Boolean = false,
    val categoryForRename: CategoryEntity? = null,
    val categoryForChangeIcon: CategoryEntity? = null,
    val categoryForDelete: CategoryEntity? = null,
    val itemForAssignCategory: ApkVaultItem? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ApkVaultDatabase.getInstance(application)
    private val categoryRepository = CategoryRepository(database.categoryDao())

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
        observeCategoriesAndMappings()
        refreshAll()
    }

    private fun observeCategoriesAndMappings() {
        viewModelScope.launch {
            combine(categoryRepository.categories, categoryRepository.mappings) { cats, maps ->
                val mappingMap = maps.associate { it.packageName to it.categoryId }
                Pair(cats, mappingMap)
            }.collect { (cats, mappingMap) ->
                _uiState.update { state ->
                    val enriched = enrichVaultItems(state.rawVaultItems, mappingMap, cats)
                    state.copy(
                        categories = cats,
                        categoryMappings = mappingMap,
                        vaultItems = enriched
                    )
                }
            }
        }
    }

    private fun enrichVaultItems(
        items: List<ApkVaultItem>,
        mappings: Map<String, Long>,
        categories: List<CategoryEntity>
    ): List<ApkVaultItem> {
        val catMap = categories.associate { it.id to it.name }
        return items.map { item ->
            val catId = mappings[item.packageName]
            val catName = catId?.let { catMap[it] }
            item.copy(categoryId = catId, categoryName = catName)
        }
    }

    fun checkPermissions() {
        val context = getApplication<Application>()
        val hasPermission = PermissionHelper.hasStorageAccess(context)
        _uiState.update { it.copy(hasStoragePermission = hasPermission) }
    }

    // Navigation Actions
    fun navigateToCategory(categoryId: Long, categoryName: String) {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.APK_LIST,
                selectedCategoryId = categoryId,
                selectedCategoryName = categoryName,
                searchQuery = ""
            )
        }
    }

    fun navigateToCategoriesHome() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.CATEGORIES,
                searchQuery = ""
            )
        }
    }

    fun navigateToInstalledApps() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.INSTALLED_APPS,
                searchQuery = ""
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setShowSystemApps(show: Boolean) {
        _uiState.update { it.copy(showSystemApps = show) }
        loadInstalledApps()
    }

    fun refreshAll() {
        scanVault()
        loadInstalledApps()
    }

    fun scanVault() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningVault = true) }
            val context = getApplication<Application>()
            val items = ApkScanner.scanStorageForApks(context)
            _uiState.update { state ->
                val enriched = enrichVaultItems(items, state.categoryMappings, state.categories)
                state.copy(
                    rawVaultItems = items,
                    vaultItems = enriched,
                    isScanningVault = false
                )
            }
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInstalled = true) }
            val context = getApplication<Application>()
            val apps = ApkExtractor.getInstalledApps(context, _uiState.value.showSystemApps)
            _uiState.update {
                it.copy(
                    installedApps = apps,
                    isLoadingInstalled = false
                )
            }
        }
    }

    // Category Dialog Management
    fun openCreateCategoryDialog() {
        _uiState.update { it.copy(showCreateCategoryDialog = true) }
    }

    fun closeCreateCategoryDialog() {
        _uiState.update { it.copy(showCreateCategoryDialog = false) }
    }

    fun createCategory(name: String, iconName: String) {
        viewModelScope.launch {
            categoryRepository.addCategory(name, iconName)
            _uiState.update {
                it.copy(
                    showCreateCategoryDialog = false,
                    userMessage = "Created category '$name'"
                )
            }
        }
    }

    fun openRenameCategoryDialog(category: CategoryEntity) {
        _uiState.update { it.copy(categoryForRename = category) }
    }

    fun closeRenameCategoryDialog() {
        _uiState.update { it.copy(categoryForRename = null) }
    }

    fun renameCategory(category: CategoryEntity, newName: String) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category.id, newName, category.iconName)
            _uiState.update {
                it.copy(
                    categoryForRename = null,
                    userMessage = "Renamed category to '$newName'"
                )
            }
        }
    }

    fun openChangeIconDialog(category: CategoryEntity) {
        _uiState.update { it.copy(categoryForChangeIcon = category) }
    }

    fun closeChangeIconDialog() {
        _uiState.update { it.copy(categoryForChangeIcon = null) }
    }

    fun changeCategoryIcon(category: CategoryEntity, newIconName: String) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category.id, category.name, newIconName)
            _uiState.update {
                it.copy(
                    categoryForChangeIcon = null,
                    userMessage = "Updated icon for '${category.name}'"
                )
            }
        }
    }

    fun openDeleteCategoryDialog(category: CategoryEntity) {
        _uiState.update { it.copy(categoryForDelete = category) }
    }

    fun closeDeleteCategoryDialog() {
        _uiState.update { it.copy(categoryForDelete = null) }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category.id)
            _uiState.update { state ->
                // If user was viewing the deleted category, navigate back to home
                val newScreen = if (state.currentScreen == AppScreen.APK_LIST && state.selectedCategoryId == category.id) {
                    AppScreen.CATEGORIES
                } else {
                    state.currentScreen
                }
                state.copy(
                    categoryForDelete = null,
                    currentScreen = newScreen,
                    userMessage = "Deleted category '${category.name}'. APKs moved to Uncategorized."
                )
            }
        }
    }

    fun openAssignCategoryDialog(item: ApkVaultItem) {
        _uiState.update { it.copy(itemForAssignCategory = item) }
    }

    fun closeAssignCategoryDialog() {
        _uiState.update { it.copy(itemForAssignCategory = null) }
    }

    fun assignCategory(item: ApkVaultItem, categoryId: Long?) {
        viewModelScope.launch {
            categoryRepository.assignCategory(item.packageName, categoryId)
            val catName = categoryId?.let { id ->
                _uiState.value.categories.find { it.id == id }?.name
            } ?: "Uncategorized"
            _uiState.update {
                it.copy(
                    itemForAssignCategory = null,
                    userMessage = "Moved '${item.label}' to $catName"
                )
            }
        }
    }

    // App Extraction & Install & Delete
    fun extractApk(app: InstalledAppItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(extractingPackage = app.packageName) }
            val context = getApplication<Application>()
            val result = ApkExtractor.extractApk(context, app)

            result.onSuccess { savedFile ->
                _uiState.update {
                    it.copy(
                        extractingPackage = null,
                        extractedInSession = it.extractedInSession + app.packageName,
                        userMessage = "Extracted to ${savedFile.name} in Downloads/APK_Archive"
                    )
                }
                scanVault()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        extractingPackage = null,
                        userMessage = "Failed to extract APK: ${err.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun installApk(context: Context, item: ApkVaultItem) {
        if (!PermissionHelper.canInstallPackages(context)) {
            val installSettingsIntent = PermissionHelper.createInstallPermissionIntent(context)
            context.startActivity(installSettingsIntent)
            _uiState.update {
                it.copy(userMessage = "Please allow installation from unknown sources for APK Vault")
            }
            return
        }

        val result = ApkScanner.installApk(context, item.file)
        result.onFailure { error ->
            _uiState.update {
                it.copy(userMessage = "Could not launch installer: ${error.localizedMessage}")
            }
        }
    }

    fun requestDelete(item: ApkVaultItem) {
        _uiState.update { it.copy(itemPendingDelete = item) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(itemPendingDelete = null) }
    }

    fun confirmDelete() {
        val item = _uiState.value.itemPendingDelete ?: return
        viewModelScope.launch {
            val success = ApkScanner.deleteApk(item.file)
            if (success) {
                _uiState.update {
                    it.copy(
                        itemPendingDelete = null,
                        userMessage = "Deleted ${item.label} APK file"
                    )
                }
                scanVault()
            } else {
                _uiState.update {
                    it.copy(
                        itemPendingDelete = null,
                        userMessage = "Failed to delete file. Check permissions."
                    )
                }
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    // Backup & Restore
    fun exportBackup(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = categoryRepository.exportBackupJson()
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(json.toByteArray(Charsets.UTF_8))
                }
                _uiState.update {
                    it.copy(userMessage = "Backup exported successfully!")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(userMessage = "Export failed: ${e.localizedMessage}")
                }
            }
        }
    }

    fun importBackup(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader().use { it.readText() }
                } ?: ""

                if (jsonString.isBlank()) {
                    _uiState.update { it.copy(userMessage = "Backup file is empty.") }
                    return@launch
                }

                val result = categoryRepository.importBackupJson(jsonString)
                result.onSuccess { (catCount, mapCount) ->
                    _uiState.update {
                        it.copy(userMessage = "Restored $catCount categories & $mapCount app mappings!")
                    }
                    scanVault()
                }.onFailure { err ->
                    _uiState.update {
                        it.copy(userMessage = "Import failed: ${err.localizedMessage ?: "Invalid backup file"}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(userMessage = "Import failed: ${e.localizedMessage}")
                }
            }
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ui.components.AssignCategoryDialog
import com.example.ui.components.ChangeCategoryIconDialog
import com.example.ui.components.CreateCategoryDialog
import com.example.ui.components.DeleteCategoryDialog
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.RenameCategoryDialog
import com.example.ui.screens.ApkVaultScreen
import com.example.ui.screens.CATEGORY_ID_ALL
import com.example.ui.screens.CATEGORY_ID_UNCATEGORIZED
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.InstalledAppsScreen
import com.example.ui.theme.ExpressiveTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.util.PermissionHelper
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Recheck permissions and scan when returning from settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Permission launcher for All Files Access
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.checkPermissions()
        viewModel.refreshAll()
    }

    fun requestStoragePermission() {
        val intent = PermissionHelper.createStorageAccessIntent(context)
        storagePermissionLauncher.launch(intent)
    }

    // Handle user snackbar notifications
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Back handling: pop back to CategoriesScreen or clear search
    BackHandler(enabled = uiState.searchQuery.isNotEmpty() || uiState.currentScreen != AppScreen.CATEGORIES) {
        if (uiState.searchQuery.isNotEmpty()) {
            viewModel.setSearchQuery("")
        } else if (uiState.currentScreen != AppScreen.CATEGORIES) {
            viewModel.navigateToCategoriesHome()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ExpressiveTheme.colors.appBackground),
        containerColor = ExpressiveTheme.colors.appBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentScreen) {
                AppScreen.CATEGORIES -> {
                    // Primary Home Screen: Categories List
                    CategoriesScreen(
                        categories = uiState.categories,
                        apkItems = uiState.vaultItems,
                        categoryMappings = uiState.categoryMappings,
                        isScanning = uiState.isScanningVault,
                        onCategoryClick = { categoryId, categoryName ->
                            viewModel.navigateToCategory(categoryId, categoryName)
                        },
                        onCreateCategoryClick = { viewModel.openCreateCategoryDialog() },
                        onRenameCategoryClick = { category -> viewModel.openRenameCategoryDialog(category) },
                        onChangeIconClick = { category -> viewModel.openChangeIconDialog(category) },
                        onDeleteCategoryClick = { category -> viewModel.openDeleteCategoryDialog(category) },
                        onOpenSearch = {
                            viewModel.navigateToCategory(CATEGORY_ID_ALL, "All APKs")
                        },
                        onOpenExtractor = {
                            viewModel.navigateToInstalledApps()
                        },
                        onRescan = { viewModel.refreshAll() },
                        onExportBackup = { uri -> viewModel.exportBackup(context, uri) },
                        onImportBackup = { uri -> viewModel.importBackup(context, uri) }
                    )
                }

                AppScreen.APK_LIST -> {
                    // Secondary Screen: Filtered APK List for the chosen Category
                    val categoryApks = remember(
                        uiState.vaultItems,
                        uiState.selectedCategoryId,
                        uiState.categoryMappings,
                        uiState.searchQuery
                    ) {
                        val baseList = when (uiState.selectedCategoryId) {
                            CATEGORY_ID_ALL -> uiState.vaultItems
                            CATEGORY_ID_UNCATEGORIZED -> uiState.vaultItems.filter {
                                val catId = uiState.categoryMappings[it.packageName]
                                catId == null || catId <= 0L
                            }
                            else -> uiState.vaultItems.filter {
                                uiState.categoryMappings[it.packageName] == uiState.selectedCategoryId
                            }
                        }

                        if (uiState.searchQuery.isBlank()) {
                            baseList
                        } else {
                            val q = uiState.searchQuery.trim().lowercase()
                            baseList.filter {
                                it.label.lowercase().contains(q) ||
                                        it.packageName.lowercase().contains(q) ||
                                        it.file.name.lowercase().contains(q)
                            }
                        }
                    }

                    ApkVaultScreen(
                        categoryTitle = uiState.selectedCategoryName,
                        items = categoryApks,
                        isScanning = uiState.isScanningVault,
                        hasStoragePermission = uiState.hasStoragePermission,
                        searchQuery = uiState.searchQuery,
                        onBackClick = { viewModel.navigateToCategoriesHome() },
                        onRequestPermission = { requestStoragePermission() },
                        onRescan = { viewModel.scanVault() },
                        onInstall = { item -> viewModel.installApk(context, item) },
                        onDelete = { item -> viewModel.requestDelete(item) },
                        onAssignCategory = { item -> viewModel.openAssignCategoryDialog(item) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onNavigateToExtract = { viewModel.navigateToInstalledApps() }
                    )
                }

                AppScreen.INSTALLED_APPS -> {
                    // Secondary Screen: Installed Apps Extractor
                    val filteredInstalled = remember(uiState.installedApps, uiState.searchQuery) {
                        if (uiState.searchQuery.isBlank()) {
                            uiState.installedApps
                        } else {
                            val q = uiState.searchQuery.trim().lowercase()
                            uiState.installedApps.filter {
                                it.label.lowercase().contains(q) ||
                                        it.packageName.lowercase().contains(q)
                            }
                        }
                    }

                    InstalledAppsScreen(
                        apps = filteredInstalled,
                        isLoading = uiState.isLoadingInstalled,
                        showSystemApps = uiState.showSystemApps,
                        searchQuery = uiState.searchQuery,
                        extractingPackage = uiState.extractingPackage,
                        extractedInSession = uiState.extractedInSession,
                        onBackClick = { viewModel.navigateToCategoriesHome() },
                        onToggleSystemApps = { viewModel.setShowSystemApps(it) },
                        onExtract = { app -> viewModel.extractApk(app) },
                        onRefresh = { viewModel.loadInstalledApps() },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) }
                    )
                }
            }
        }
    }

    // Dialog: Create Category
    if (uiState.showCreateCategoryDialog) {
        CreateCategoryDialog(
            onDismiss = { viewModel.closeCreateCategoryDialog() },
            onConfirm = { name, iconName ->
                viewModel.createCategory(name, iconName)
            }
        )
    }

    // Dialog: Rename Category
    uiState.categoryForRename?.let { category ->
        RenameCategoryDialog(
            category = category,
            onDismiss = { viewModel.closeRenameCategoryDialog() },
            onConfirm = { newName ->
                viewModel.renameCategory(category, newName)
            }
        )
    }

    // Dialog: Change Category Icon
    uiState.categoryForChangeIcon?.let { category ->
        ChangeCategoryIconDialog(
            category = category,
            onDismiss = { viewModel.closeChangeIconDialog() },
            onConfirm = { newIconName ->
                viewModel.changeCategoryIcon(category, newIconName)
            }
        )
    }

    // Dialog: Delete Category (Safe: moves APKs to Uncategorized)
    uiState.categoryForDelete?.let { category ->
        DeleteCategoryDialog(
            category = category,
            onDismiss = { viewModel.closeDeleteCategoryDialog() },
            onConfirm = {
                viewModel.deleteCategory(category)
            }
        )
    }

    // Dialog: Assign Category to APK
    uiState.itemForAssignCategory?.let { item ->
        AssignCategoryDialog(
            appName = item.label,
            currentCategoryId = item.categoryId,
            categories = uiState.categories,
            onDismiss = { viewModel.closeAssignCategoryDialog() },
            onCategorySelected = { categoryId ->
                viewModel.assignCategory(item, categoryId)
            }
        )
    }

    // Dialog: Confirmation for APK File Deletion
    uiState.itemPendingDelete?.let { item ->
        DeleteConfirmationDialog(
            item = item,
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.cancelDelete() }
        )
    }
}

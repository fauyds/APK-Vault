package com.example.model

import com.example.data.entity.CategoryEntity

sealed class CategoryFilter {
    object All : CategoryFilter() {
        override val displayName: String = "All"
        override val id: Long? = null
    }

    object Uncategorized : CategoryFilter() {
        override val displayName: String = "Uncategorized"
        override val id: Long? = -1L
    }

    data class Custom(val category: CategoryEntity) : CategoryFilter() {
        override val displayName: String = category.name
        override val id: Long = category.id
    }

    abstract val displayName: String
    abstract val id: Long?
}

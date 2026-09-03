package com.example.sieve_of_wisdom.data.local.relation

import androidx.room.Embedded
import com.example.sieve_of_wisdom.data.local.entity.CategoryEntity

data class CategoryWithAccess(
    @Embedded
    val category: CategoryEntity,

    val isUnlocked: Boolean,

    val quantity: Int
)
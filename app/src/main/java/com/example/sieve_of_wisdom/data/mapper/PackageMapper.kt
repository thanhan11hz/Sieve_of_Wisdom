package com.example.sieve_of_wisdom.data.mapper

import com.example.sieve_of_wisdom.data.local.entity.AccessEntity
import com.example.sieve_of_wisdom.data.local.relation.CategoryWithAccess
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.data.remote.dto.AccessDto

fun CategoryWithAccess.toModel(): Package {
    return Package(
        categoryId = category.id,
        name = category.name,
        classification = category.classification,
        isUnlocked = isUnlocked,
        price = category.price
    )
}

fun AccessDto.toEntity(isSynced: Boolean = true): AccessEntity {
    return AccessEntity(
        userId = userId,
        categoryId = categoryId,
        isSynced = isSynced
    )
}
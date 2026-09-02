package com.example.sieve_of_wisdom.data.mapper

import com.example.sieve_of_wisdom.data.local.entity.CategoryEntity
import com.example.sieve_of_wisdom.data.remote.dto.CategoryDto

fun CategoryDto.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        classification = classification,
        price = price
    )
}
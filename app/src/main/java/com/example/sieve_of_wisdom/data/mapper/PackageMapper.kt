package com.example.sieve_of_wisdom.data.mapper

import com.example.sieve_of_wisdom.data.local.relation.CategoryWithAccess
import com.example.sieve_of_wisdom.data.model.Package

fun CategoryWithAccess.toModel(): Package {
    return Package(
        categoryId = category.id,
        classification = category.classification,
        status = status
    )
}
package com.example.sieve_of_wisdom.data.mapper

import com.example.sieve_of_wisdom.data.local.entity.UserEntity
import com.example.sieve_of_wisdom.data.model.Profile

fun UserEntity.toModel(): Profile {
    return Profile(
        id = id,
        name = name,
        email = email,
        coin = coin
    )
}
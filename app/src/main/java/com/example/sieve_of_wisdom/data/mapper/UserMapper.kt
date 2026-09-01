package com.example.sieve_of_wisdom.data.mapper

import com.example.sieve_of_wisdom.data.local.entity.UserEntity
import com.example.sieve_of_wisdom.data.model.Profile
import com.example.sieve_of_wisdom.data.remote.dto.UserDto

fun UserEntity.toModel(): Profile {
    return Profile(
        id = id,
        username = username,
        email = email,
        coin = coin
    )
}

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        email = email,
        coin = coin
    )
}

fun UserDto.toModel(): Profile {
    return Profile(
        id = id,
        username = username,
        email = email,
        coin = coin
    )
}
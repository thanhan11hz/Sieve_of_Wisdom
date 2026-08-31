package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.sieve_of_wisdom.data.local.entity.CategoryEntity
import com.example.sieve_of_wisdom.data.local.relation.CategoryWithAccess

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    suspend fun getAllCategory(): List<CategoryEntity>;

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            Access.status
        FROM Category
        JOIN Access
            ON Category.id = Access.category_id
        WHERE Access.user_id = :userId
    """)
    suspend fun getCategoryByStatus(userId: Int): List<CategoryWithAccess>;
}
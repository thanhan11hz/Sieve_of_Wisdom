package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.sieve_of_wisdom.data.local.entity.CategoryEntity
import com.example.sieve_of_wisdom.data.local.relation.CategoryWithAccess

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Category")
    suspend fun getAllCategory(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>);

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            CASE 
                WHEN Access.category_id IS NOT NULL THEN 1
                ELSE 0
            END AS isUnlocked
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
            AND Access.user_id = :userId
        ORDER BY Category.id
    """)
    suspend fun getCategoryWithAccess(
        userId: Int
    ): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            CASE 
                WHEN Access.category_id IS NOT NULL THEN 1
                ELSE 0
            END AS isUnlocked
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
            AND Access.user_id = :userId
        WHERE Category.name LIKE '%' || :query || '%'
        ORDER BY Category.id
    """)
    suspend fun getCategoryBySearch(
        userId: Int,
        query: String
    ): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            CASE 
                WHEN Access.category_id IS NOT NULL THEN 1
                ELSE 0
            END AS isUnlocked
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
            AND Access.user_id = :userId
        WHERE Category.classification = :classification
        ORDER BY Category.id
    """)
    suspend fun getCategoryByClassification(
        userId: Int,
        classification: String
    ): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            0 AS isUnlocked
        FROM Category
        WHERE NOT EXISTS (
            SELECT 1
            FROM Access
            WHERE Access.category_id = Category.id
            AND Access.user_id = :userId
        )
        ORDER BY Category.id
    """)
    suspend fun getLockedCategory(
        userId: Int
    ): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            1 AS isUnlocked
        FROM Category
        INNER JOIN Access
            ON Category.id = Access.category_id
        WHERE Access.user_id = :userId
        ORDER BY Category.id
    """)
    suspend fun getUnlockedCategory(
        userId: Int
    ): List<CategoryWithAccess>

    @Query("SELECT DISTINCT classification FROM Category")
    suspend fun getAllClassifications(): List<String>


}
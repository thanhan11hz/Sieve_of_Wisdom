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
    suspend fun getAllCategory(): List<CategoryEntity>;

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
    """)
    suspend fun getCategoryWithAccess(): List<CategoryWithAccess>;

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
        WHERE Category.name LIKE '%' || :query || "%"
    """)
    suspend fun getCategoryBySearch(query: String): List<CategoryWithAccess>;

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
        WHERE Category.classification = :classification
    """)
    suspend fun getCategoryByClassification(classification: String): List<CategoryWithAccess>;

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            0 AS isUnlocked
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
        WHERE Access.category_id IS NULL
    """)
    suspend fun getLockedCategory(): List<CategoryWithAccess>;

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            1 AS isUnlocked
        FROM Category
        JOIN Access
            ON Category.id = Access.category_id
        WHERE Access.category_id IS NOT NULL
    """)
    suspend fun getUnlockedCategory(): List<CategoryWithAccess>;
}
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

    @Query("SELECT * FROM Category WHERE id = :id")
    suspend fun getCategoryByID(id: Int): CategoryEntity

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            CASE 
                WHEN Access.category_id IS NOT NULL THEN 1
                ELSE 0
            END AS isUnlocked,
            COUNT(Question.id) as quantity
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
        LEFT JOIN Question
            ON Question.category_id = Category.id
        GROUP BY Category.id, Category.name, Category.classification, Category.price, Access.category_id
        ORDER BY Category.id
    """)
    suspend fun getCategoryWithAccess(): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            CASE 
                WHEN Access.category_id IS NOT NULL THEN 1
                ELSE 0
            END AS isUnlocked,
            COUNT(Question.id) as quantity
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
        LEFT JOIN Question
            ON Question.category_id = Category.id
        WHERE Category.name LIKE '%' || :query || "%"
        GROUP BY Category.id, Category.name, Category.classification, Category.price, Access.category_id
        ORDER BY Category.id
    """)
    suspend fun getCategoryBySearch(query: String): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            CASE 
                WHEN Access.category_id IS NOT NULL THEN 1
                ELSE 0
            END AS isUnlocked,
            COUNT(Question.id) as quantity
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
        LEFT JOIN Question
            ON Question.category_id = Category.id
        WHERE Category.classification = :classification
        GROUP BY Category.id, Category.name, Category.classification, Category.price, Access.category_id
        ORDER BY Category.id
    """)
    suspend fun getCategoryByClassification(classification: String): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            0 AS isUnlocked,
            COUNT(Question.id) as quantity
        FROM Category
        LEFT JOIN Access
            ON Category.id = Access.category_id
        LEFT JOIN Question
            ON Question.category_id = Category.id
        WHERE Access.category_id IS NULL
        GROUP BY Category.id, Category.name, Category.classification, Category.price, Access.category_id
        ORDER BY Category.id
    """)
    suspend fun getLockedCategory(): List<CategoryWithAccess>

    @Transaction
    @Query("""
        SELECT 
            Category.*,
            1 AS isUnlocked,
            COUNT(Question.id) as quantity
        FROM Category
        INNER JOIN Access
            ON Category.id = Access.category_id
        LEFT JOIN Question
            ON Question.category_id = Category.id
        WHERE Access.category_id IS NOT NULL
        GROUP BY Category.id, Category.name, Category.classification, Category.price, Access.category_id
        ORDER BY Category.id
    """)
    suspend fun getUnlockedCategory(): List<CategoryWithAccess>

    @Query("SELECT DISTINCT classification FROM Category")
    suspend fun getAllClassifications(): List<String>
}
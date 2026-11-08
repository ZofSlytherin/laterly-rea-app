package com.laterly.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class CollectionCount(
    val collectionName: String,
    val count: Int,
    val latestImageUrl: String?
)

@Dao
interface SavedItemDao {
    @Query("SELECT * FROM saved_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<SavedItem>>

    @Query("SELECT * FROM saved_items WHERE collectionName = :collection ORDER BY createdAt DESC")
    fun getItemsByCollection(collection: String): Flow<List<SavedItem>>

    @Query("SELECT DISTINCT collectionName FROM saved_items WHERE collectionName != '' ORDER BY collectionName ASC")
    fun getAllCollections(): Flow<List<String>>

    @Query("""
        SELECT collectionName, 
               COUNT(*) as count, 
               MAX(CASE WHEN imageUrl IS NOT NULL AND imageUrl != '' THEN imageUrl ELSE NULL END) as latestImageUrl
        FROM saved_items 
        WHERE collectionName != '' 
        GROUP BY collectionName 
        ORDER BY count DESC
    """)
    fun getCollectionSummaries(): Flow<List<CollectionCount>>

    @Query("""
        SELECT * FROM saved_items 
        WHERE (:collection IS NULL OR collectionName = :collection)
          AND (:tag IS NULL OR tags LIKE '%' || :tag || '%')
          AND (:query = '' OR title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun filterItems(collection: String?, tag: String?, query: String): Flow<List<SavedItem>>

    @Query("SELECT * FROM saved_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): SavedItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SavedItem): Long

    @Update
    suspend fun updateItem(item: SavedItem)

    @Delete
    suspend fun deleteItem(item: SavedItem)
}

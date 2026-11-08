package com.laterly.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a saved content item (link, note, article, etc.).
 * Designed for local-first persistence with categorization, notes, tags, reading stats,
 * and extracted readable article content for offline reading and future AI summarization.
 */
@Entity(tableName = "saved_items")
data class SavedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val collectionName: String = "Inbox",
    val tags: String = "", // Comma-separated tags
    val personalNotes: String = "", // User notes & takeaways
    val articleContent: String = "", // Cleaned readable article body (Reader Mode & AI input)
    val author: String = "", // Author / publication source
    val estimatedReadMinutes: Int = 3,
    val wordCount: Int = 0,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

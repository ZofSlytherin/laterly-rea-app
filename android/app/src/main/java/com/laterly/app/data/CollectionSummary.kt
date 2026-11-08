package com.laterly.app.data

data class CollectionSummary(
    val name: String,
    val count: Int,
    val coverImageUrl: String?,
    val iconEmoji: String = "📁"
)


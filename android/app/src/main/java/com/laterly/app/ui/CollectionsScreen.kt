package com.laterly.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.laterly.app.FilterChip
import com.laterly.app.data.CollectionCount
import com.laterly.app.data.SavedItemDao
import com.laterly.app.ui.theme.LaterlyPrimary
import com.laterly.app.ui.theme.LaterlySurfaceContainer
import com.laterly.app.ui.theme.LaterlySurfaceContainerHigh

/**
 * 2-column Collections Screen matching collection_screen.png:
 * Top intro header, filter chips (All, Pinned, Shared, Archived),
 * 2-column card grid with icon overlay badge and item counts, and circular terracotta FAB.
 */
@Composable
fun CollectionsScreen(
    dao: SavedItemDao,
    onCollectionClick: (String) -> Unit,
    onAddCollectionClick: () -> Unit
) {
    val collectionCounts by dao.getCollectionSummaries().collectAsState(initial = emptyList())
    var selectedFilter by remember { mutableStateOf("All") }
    val defaultCollections = listOf(
        CollectionCount("Recipes", 0, null),
        CollectionCount("Reading List", 0, null),
        CollectionCount("Travel Places", 0, null),
        CollectionCount("Tech Watchlist", 0, null),
        CollectionCount("Design Inspirations", 0, null)
    )

    // Merge database counts with defaults so all spaces appear
    val displayCollections = remember(collectionCounts) {
        val countMap = collectionCounts.associateBy { it.collectionName }
        val merged = mutableListOf<CollectionCount>()
        defaultCollections.forEach { def ->
            val found = countMap[def.collectionName]
            merged.add(found ?: def)
        }
        // Add any user-created custom collections not in defaults
        collectionCounts.forEach { item ->
            if (merged.none { it.collectionName == item.collectionName }) {
                merged.add(item)
            }
        }
        merged
    }

    val totalActiveCount = displayCollections.size

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCollectionClick,
                containerColor = LaterlyPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Text("＋", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Section
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Collections",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalActiveCount active spaces to keep life beautifully sorted",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Segmented Filter Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            label = "All ($totalActiveCount)",
                            isSelected = selectedFilter == "All",
                            onClick = { selectedFilter = "All" }
                        )
                        FilterChip(
                            label = "Pinned",
                            isSelected = selectedFilter == "Pinned",
                            onClick = { selectedFilter = "Pinned" }
                        )
                        FilterChip(
                            label = "Shared",
                            isSelected = selectedFilter == "Shared",
                            onClick = { selectedFilter = "Shared" }
                        )
                        FilterChip(
                            label = "Archived",
                            isSelected = selectedFilter == "Archived",
                            onClick = { selectedFilter = "Archived" }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // 2-Column Collection Cards
            items(displayCollections, key = { it.collectionName }) { col ->
                CollectionCard(
                    collection = col,
                    onClick = { onCollectionClick(col.collectionName) }
                )
            }
        }
    }
}

@Composable
fun CollectionCard(
    collection: CollectionCount,
    onClick: () -> Unit
) {
    val iconEmoji = when (collection.collectionName) {
        "Recipes" -> "🍴"
        "Reading List", "Reading" -> "📖"
        "Travel Places", "Travel" -> "✈"
        "Tech Watchlist", "Tech" -> "💻"
        "Design Inspirations", "Inspiration" -> "🎨"
        else -> "📁"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LaterlySurfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Top Image Container with floating Category Icon badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .background(LaterlySurfaceContainerHigh)
            ) {
                if (!collection.latestImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = collection.latestImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback visual illustration
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iconEmoji, fontSize = 36.sp)
                    }
                }

                // Top right pinned circular icon badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iconEmoji, fontSize = 14.sp)
                }
            }

            // Bottom Labels
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = collection.collectionName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${collection.count} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


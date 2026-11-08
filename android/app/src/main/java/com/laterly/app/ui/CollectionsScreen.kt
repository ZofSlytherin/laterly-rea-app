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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.laterly.app.ui.theme.NdAccent
import com.laterly.app.ui.theme.NdBorder
import com.laterly.app.ui.theme.NdTextDisabled
import com.laterly.app.ui.theme.NdTextSecondary
import com.laterly.app.ui.theme.SpaceGrotesk
import com.laterly.app.ui.theme.SpaceMono

/**
 * Collections screen — Nothing design system.
 * 2-column grid, flat cards with hairline borders, Space Mono labels.
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

    val displayCollections = remember(collectionCounts) {
        val countMap = collectionCounts.associateBy { it.collectionName }
        val merged = mutableListOf<CollectionCount>()
        defaultCollections.forEach { def ->
            merged.add(countMap[def.collectionName] ?: def)
        }
        collectionCounts.forEach { item ->
            if (merged.none { it.collectionName == item.collectionName }) merged.add(item)
        }
        merged
    }

    val totalCount = displayCollections.size

    Scaffold(
        floatingActionButton = {
            // FAB — accent red, square (technical), icon only
            FloatingActionButton(
                onClick = onAddCollectionClick,
                containerColor = NdAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Save new item",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                    // Count — the hero number on this screen
                    Text(
                        text = "$totalCount",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Medium,
                        fontSize = 48.sp,
                        letterSpacing = (-1).sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 48.sp
                    )
                    Text(
                        text = "SPACES",
                        fontFamily = SpaceMono,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = NdTextDisabled
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Filter chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("All", "Pinned", "Shared", "Archived").forEach { filter ->
                            FilterChip(
                                label = filter.uppercase(),
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // 2-column collection cards
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
    // Monochrome icon initial instead of emoji
    val initial = collection.collectionName.take(1).uppercase()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
    ) {
        // Image area — flat, no rounded insets
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            if (!collection.latestImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = collection.latestImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark overlay so label is readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
            } else {
                // Initial letter — large, centered, Space Grotesk
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Medium,
                        fontSize = 36.sp,
                        color = NdTextDisabled
                    )
                }
            }
        }

        // Labels — compact, tight spacing
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = collection.collectionName,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${collection.count} ITEMS",
                fontFamily = SpaceMono,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                color = NdTextDisabled
            )
        }
    }
}

package com.laterly.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.laterly.app.data.AppDatabase
import com.laterly.app.data.SavedItem
import com.laterly.app.data.SavedItemDao
import com.laterly.app.ui.CollectionsScreen
import com.laterly.app.ui.SettingsScreen
import com.laterly.app.ui.theme.BadgeArticle
import com.laterly.app.ui.theme.BadgeRecipe
import com.laterly.app.ui.theme.BadgeVideo
import com.laterly.app.ui.theme.LaterlyPrimary
import com.laterly.app.ui.theme.LaterlySecondary
import com.laterly.app.ui.theme.LaterlySurface
import com.laterly.app.ui.theme.LaterlySurfaceContainer
import com.laterly.app.ui.theme.LaterlySurfaceContainerHigh
import com.laterly.app.ui.theme.LaterlySurfaceContainerLow
import com.laterly.app.ui.theme.LaterlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val dao = database.savedItemDao()

        setContent {
            LaterlyTheme {
                MainAppNavigation(dao = dao)
            }
        }
    }
}

/**
 * Host container with docked 4-tab bottom navigation bar:
 * 0: Home Feed
 * 1: Collections Grid
 * 2: Search View
 * 3: Settings View
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavigation(dao: SavedItemDao) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeCollectionFilter by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val screenTitle = when (selectedTab) {
        0 -> "Home"
        1 -> "Collections"
        2 -> "Search"
        3 -> "Settings"
        else -> "Laterly"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "laterly.",
                            color = LaterlyPrimary,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = screenTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { selectedTab = 3 },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = LaterlyPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LaterlySurface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LaterlySurface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavTab(
                        label = "Home",
                        activeIcon = Icons.Filled.Home,
                        inactiveIcon = Icons.Outlined.Home,
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            activeCollectionFilter = null
                        }
                    )
                    BottomNavTab(
                        label = "Collections",
                        activeIcon = Icons.Filled.Folder,
                        inactiveIcon = Icons.Outlined.Folder,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    BottomNavTab(
                        label = "Search",
                        activeIcon = Icons.Filled.Search,
                        inactiveIcon = Icons.Outlined.Search,
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                    BottomNavTab(
                        label = "Settings",
                        activeIcon = Icons.Filled.Settings,
                        inactiveIcon = Icons.Outlined.Settings,
                        isSelected = selectedTab == 3,
                        onClick = { selectedTab = 3 }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeFeedScreen(
                    dao = dao,
                    presetCollection = activeCollectionFilter,
                    onItemClick = { item ->
                        val intent = Intent(context, DetailActivity::class.java).apply {
                            putExtra("ITEM_ID", item.id)
                        }
                        context.startActivity(intent)
                    }
                )
                1 -> CollectionsScreen(
                    dao = dao,
                    onCollectionClick = { colName ->
                        activeCollectionFilter = colName
                        selectedTab = 0
                    },
                    onAddCollectionClick = {
                        val shareIntent = Intent(context, ShareReceiverActivity::class.java)
                        context.startActivity(shareIntent)
                    }
                )
                2 -> SearchDiscoveryScreen(
                    dao = dao,
                    onItemClick = { item ->
                        val intent = Intent(context, DetailActivity::class.java).apply {
                            putExtra("ITEM_ID", item.id)
                        }
                        context.startActivity(intent)
                    }
                )
                3 -> SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomNavTab(
    label: String,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(LaterlySecondary)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = activeIcon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = inactiveIcon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun HomeFeedScreen(
    dao: SavedItemDao,
    presetCollection: String?,
    onItemClick: (SavedItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCollection by remember(presetCollection) { mutableStateOf(presetCollection) }
    var selectedTag by remember { mutableStateOf<String?>(null) }

    val collections by dao.getAllCollections().collectAsState(initial = emptyList())
    val items by dao.filterItems(
        collection = selectedCollection,
        tag = selectedTag,
        query = searchQuery.trim()
    ).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar - 4px grid spacing, consistent pill shape
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Search saved links, articles, notes...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = LaterlyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LaterlySurfaceContainerLow,
                unfocusedContainerColor = LaterlySurfaceContainerLow,
                focusedBorderColor = LaterlyPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        // Filter chips carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = "All Items",
                isSelected = selectedCollection == null && selectedTag == null,
                onClick = {
                    selectedCollection = null
                    selectedTag = null
                }
            )

            collections.forEach { colName ->
                FilterChip(
                    label = colName,
                    isSelected = selectedCollection == colName,
                    onClick = {
                        selectedCollection = if (selectedCollection == colName) null else colName
                    }
                )
            }

            if (selectedTag != null) {
                FilterChip(
                    label = "#$selectedTag ✕",
                    isSelected = true,
                    onClick = { selectedTag = null }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isBlank() && selectedCollection == null && selectedTag == null)
                        "No saves yet.\nShare a link to Laterly from any browser or app!"
                    else
                        "No matching saves found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    SavedItemRow(
                        item = item,
                        onClick = { onItemClick(item) },
                        onTagClick = { tag ->
                            selectedTag = if (selectedTag == tag) null else tag
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

/**
 * Dedicated Search & Discovery view
 */
@Composable
fun SearchDiscoveryScreen(
    dao: SavedItemDao,
    onItemClick: (SavedItem) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val items by dao.filterItems(null, null, query.trim()).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    text = "Search by keyword or tag...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = LaterlyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LaterlySurfaceContainerLow,
                unfocusedContainerColor = LaterlySurfaceContainerLow,
                focusedBorderColor = LaterlyPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Text(
            text = "Results (${items.size})",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                SavedItemRow(
                    item = item,
                    onClick = { onItemClick(item) },
                    onTagClick = {}
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) LaterlyPrimary else LaterlySurfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SavedItemRow(
    item: SavedItem,
    onClick: () -> Unit,
    onTagClick: (String) -> Unit
) {
    val badgeColor = when (item.collectionName.lowercase()) {
        "recipes" -> BadgeRecipe
        "video", "videos" -> BadgeVideo
        else -> BadgeArticle.copy(alpha = 0.2f)
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
            if (!item.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeColor)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.collectionName,
                            color = LaterlyPrimary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    val domain = Uri.parse(item.url).host ?: item.url
                    Text(
                        text = domain,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!item.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (item.tags.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.bodySmall,
                                color = LaterlyPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(LaterlySurfaceContainerHigh)
                                    .clickable { onTagClick(tag) }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.laterly.app.ui.theme.LaterlyTheme
import com.laterly.app.ui.theme.NdAccent
import com.laterly.app.ui.theme.NdBorder
import com.laterly.app.ui.theme.NdTextDisabled
import com.laterly.app.ui.theme.NdTextSecondary
import com.laterly.app.ui.theme.SpaceGrotesk
import com.laterly.app.ui.theme.SpaceMono

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
 * Host container with docked 4-tab bottom navigation bar.
 */
@Composable
fun MainAppNavigation(dao: SavedItemDao) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeCollectionFilter by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            // Nothing-style top bar: flat, no elevation, hairline border bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand wordmark — Space Grotesk, lowercase, accent dot
                Text(
                    text = "laterly.",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                // Settings nav shortcut
                IconButton(onClick = { selectedTab = 3 }) {
                    Icon(
                        imageVector = if (selectedTab == 3) Icons.Filled.Settings else Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = if (selectedTab == 3) MaterialTheme.colorScheme.primary else NdTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        bottomBar = {
            // Nothing-style nav: flat bar, hairline top border, icon-only with dot indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Top hairline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(NdBorder)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NdNavTab("Home",        Icons.Filled.Home,       Icons.Outlined.Home,       selectedTab == 0) {
                        selectedTab = 0; activeCollectionFilter = null
                    }
                    NdNavTab("Collections", Icons.Filled.Folder,     Icons.Outlined.Folder,     selectedTab == 1) { selectedTab = 1 }
                    NdNavTab("Search",      Icons.Filled.Search,     Icons.Outlined.Search,     selectedTab == 2) { selectedTab = 2 }
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

/**
 * Nothing-style nav tab: icon + a 4dp red dot below when active.
 * No filled pill, no label text — navigation is tertiary UI.
 */
@Composable
fun NdNavTab(
    label: String,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (isSelected) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.onBackground else NdTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        // Active indicator: 4×4 red dot — the single accent moment in nav
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(
                    color = if (isSelected) NdAccent else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search field — flat surface, hairline border, Space Grotesk placeholder
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NdTextDisabled
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = NdTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = NdTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedBorderColor      = NdAccent,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips — Space Mono ALL CAPS labels, minimal pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = "ALL",
                isSelected = selectedCollection == null && selectedTag == null,
                onClick = { selectedCollection = null; selectedTag = null }
            )
            collections.forEach { colName ->
                FilterChip(
                    label = colName.uppercase(),
                    isSelected = selectedCollection == colName,
                    onClick = {
                        selectedCollection = if (selectedCollection == colName) null else colName
                    }
                )
            }
            if (selectedTag != null) {
                FilterChip(
                    label = "#${selectedTag!!.uppercase()} ✕",
                    isSelected = true,
                    onClick = { selectedTag = null }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            // Empty state: text-only, no sad illustration
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (searchQuery.isBlank() && selectedCollection == null && selectedTag == null)
                            "[EMPTY]" else "[NO RESULTS]",
                        fontFamily = SpaceMono,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = NdTextDisabled
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (searchQuery.isBlank() && selectedCollection == null && selectedTag == null)
                            "Share a link to Laterly from any app"
                        else
                            "No saves match this filter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NdTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp) // hairline-gap list
            ) {
                items(items, key = { it.id }) { item ->
                    SavedItemRow(
                        item = item,
                        onClick = { onItemClick(item) },
                        onTagClick = { tag ->
                            selectedTag = if (selectedTag == tag) null else tag
                        }
                    )
                    // Hairline divider between rows
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

/** Dedicated Search & Discovery view */
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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NdTextDisabled
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = NdTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = NdTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedBorderColor      = NdAccent,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Result count — Space Mono label
        Text(
            text = "${items.size} RESULTS",
            fontFamily = SpaceMono,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = NdTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(items, key = { it.id }) { item ->
                SavedItemRow(
                    item = item,
                    onClick = { onItemClick(item) },
                    onTagClick = {}
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * Nothing-style filter chip: flat, technical corner (8dp), Space Mono ALL CAPS text.
 * Active state: white/black background with matching text — not terracotta.
 * The red accent is reserved for the single meaningful moment per screen.
 */
@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.surfaceContainer
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontFamily = SpaceMono,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            color = if (isSelected) MaterialTheme.colorScheme.background else NdTextSecondary
        )
    }
}

/**
 * Nothing-style saved item row: flat surface, no card shadow.
 * Three-layer hierarchy: title (primary) → description (secondary) → domain+tags (tertiary).
 * Thumbnail optional on the left; thumbnail uses 60×60 technical square.
 */
@Composable
fun SavedItemRow(
    item: SavedItem,
    onClick: () -> Unit,
    onTagClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Thumbnail — technical square, no rounded corners (Nothing aesthetic)
        if (!item.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentScale = ContentScale.Crop
            )
        } else {
            // Dot-matrix placeholder — 60×60 muted surface
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ ]",
                    fontFamily = SpaceMono,
                    fontSize = 10.sp,
                    color = NdTextDisabled
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tertiary: domain + collection — Space Mono ALL CAPS
            val domain = Uri.parse(item.url).host ?: ""
            Text(
                text = "${domain.uppercase()} · ${item.collectionName.uppercase()}",
                fontFamily = SpaceMono,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = NdTextDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Primary: title — Space Grotesk Medium, 2 lines max
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Secondary: description — Space Grotesk Regular, muted
            if (!item.description.isNullOrBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NdTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tags — Space Mono, inline, no pill background
            if (item.tags.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.tags.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .take(3) // limit visible tags to avoid visual noise
                        .forEach { tag ->
                            Text(
                                text = "#$tag",
                                fontFamily = SpaceMono,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                color = NdTextDisabled,
                                modifier = Modifier.clickable { onTagClick(tag) }
                            )
                        }
                }
            }
        }
    }
}

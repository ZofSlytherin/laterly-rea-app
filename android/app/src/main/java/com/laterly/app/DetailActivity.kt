package com.laterly.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.laterly.app.ui.theme.BadgeArticle
import com.laterly.app.ui.theme.DeleteContainer
import com.laterly.app.ui.theme.DeleteContent
import com.laterly.app.ui.theme.LaterlyPrimary
import com.laterly.app.ui.theme.LaterlySurface
import com.laterly.app.ui.theme.LaterlySurfaceContainer
import com.laterly.app.ui.theme.LaterlySurfaceContainerHigh
import com.laterly.app.ui.theme.LaterlyTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent.getLongExtra("ITEM_ID", -1L)
        if (itemId == -1L) {
            finish()
            return
        }

        val db = AppDatabase.getDatabase(this)
        val dao = db.savedItemDao()

        setContent {
            LaterlyTheme {
                DetailScreen(
                    itemId = itemId,
                    dao = dao,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: Long,
    dao: com.laterly.app.data.SavedItemDao,
    onBack: () -> Unit
) {
    var item by remember { mutableStateOf<SavedItem?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showEditNotesDialog by remember { mutableStateOf(false) }
    var showMoveDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        item = dao.getItemById(itemId)
    }

    val currentItem = item ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Loading save...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val dateFormat = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
    val formattedDate = remember(currentItem.createdAt) { dateFormat.format(Date(currentItem.createdAt)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == 0) "Save Details" else "Reader View",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LaterlySurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = LaterlyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val updated = currentItem.copy(isFavorite = !currentItem.isFavorite)
                            item = updated
                            coroutineScope.launch { dao.updateItem(updated) }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (currentItem.isFavorite) LaterlyPrimary else LaterlySurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentItem.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (currentItem.isFavorite) Color.White else LaterlyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LaterlySurface)
            )
        },
        bottomBar = {
            // Floating 4-button action dock
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = CircleShape,
                color = LaterlySurfaceContainer,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionDockButton(
                        label = "Edit",
                        icon = Icons.Default.Edit,
                        onClick = { showEditNotesDialog = true }
                    )
                    ActionDockButton(
                        label = "Move",
                        icon = Icons.Default.Folder,
                        onClick = { showMoveDialog = true }
                    )
                    ActionDockButton(
                        label = "Share",
                        icon = Icons.Default.Share,
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${currentItem.title}\n${currentItem.url}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Link"))
                        }
                    )
                    ActionDockButton(
                        label = "Delete",
                        icon = Icons.Default.Delete,
                        isDestructive = true,
                        onClick = { showDeleteConfirmDialog = true }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mode Selector Tabs (Overview vs Reader View)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = LaterlySurface,
                contentColor = LaterlyPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = LaterlyPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Overview & Notes",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (currentItem.articleContent.isNotBlank()) "Reader View" else "Reader (Unavailable)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // TAB 0: Overview & Notes View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Preview Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LaterlySurfaceContainerHigh),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (!currentItem.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = currentItem.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                                            startY = 140f
                                        )
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.9f))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = currentItem.collectionName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LaterlyPrimary
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                val domain = Uri.parse(currentItem.url).host ?: currentItem.url
                                Text(
                                    text = domain,
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentItem.title,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Visit Source Tile
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.url))
                                context.startActivity(browserIntent)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LaterlySurfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(LaterlyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Visit Original Source",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                val domain = Uri.parse(currentItem.url).host ?: currentItem.url
                                Text(
                                    text = "$domain • ${currentItem.estimatedReadMinutes} min read",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Metadata 2x2 Grid
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LaterlySurfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Save Metadata",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetadataStatTile(modifier = Modifier.weight(1f), label = "Added Date", value = formattedDate)
                                MetadataStatTile(modifier = Modifier.weight(1f), label = "Reading Time", value = "${currentItem.estimatedReadMinutes} min")
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetadataStatTile(modifier = Modifier.weight(1f), label = "Source", value = currentItem.author.ifBlank { "Web Article" })
                                MetadataStatTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Word Count",
                                    value = if (currentItem.wordCount > 0) "~${currentItem.wordCount}" else "N/A"
                                )
                            }
                        }
                    }

                    // Personal Notes Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LaterlySurfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Personal Notes",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Edit",
                                    color = LaterlyPrimary,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.clickable { showEditNotesDialog = true }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(LaterlySurfaceContainerHigh)
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = if (currentItem.personalNotes.isNotBlank())
                                        currentItem.personalNotes
                                    else
                                        "No personal notes added yet. Tap Edit to record takeaways, quotes, or thoughts.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (currentItem.personalNotes.isNotBlank())
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Assigned Collection Tile
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LaterlySurfaceContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Assigned Space",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(LaterlySurfaceContainerHigh)
                                    .clickable { showMoveDialog = true }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BadgeArticle.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = LaterlyPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = currentItem.collectionName,
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Change",
                                    color = LaterlyPrimary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(72.dp))
                }
            } else {
                // TAB 1: Reader View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = currentItem.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${if (currentItem.author.isNotBlank()) "By ${currentItem.author} • " else ""}${currentItem.estimatedReadMinutes} min read",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentItem.articleContent.isNotBlank()) {
                        Text(
                            text = currentItem.articleContent,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Full reader text is unavailable for this link.\nYou can read it directly via 'Visit Original Source'.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Edit Notes Dialog
    if (showEditNotesDialog) {
        var noteDraft by remember { mutableStateOf(currentItem.personalNotes) }
        AlertDialog(
            onDismissRequest = { showEditNotesDialog = false },
            title = { Text("Personal Notes", style = MaterialTheme.typography.titleLarge) },
            text = {
                OutlinedTextField(
                    value = noteDraft,
                    onValueChange = { noteDraft = it },
                    placeholder = { Text("Add takeaways or quotes...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = currentItem.copy(personalNotes = noteDraft)
                        item = updated
                        coroutineScope.launch { dao.updateItem(updated) }
                        showEditNotesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LaterlyPrimary)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditNotesDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Move Collection Dialog
    if (showMoveDialog) {
        val collections = listOf("Inbox", "Recipes", "Reading", "Travel", "Tech", "Inspiration")
        AlertDialog(
            onDismissRequest = { showMoveDialog = false },
            title = { Text("Move to Space", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    collections.forEach { col ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (currentItem.collectionName == col) LaterlyPrimary.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable {
                                    val updated = currentItem.copy(collectionName = col)
                                    item = updated
                                    coroutineScope.launch { dao.updateItem(updated) }
                                    showMoveDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = col,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (currentItem.collectionName == col) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentItem.collectionName == col) LaterlyPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMoveDialog = false }) { Text("Close") }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Save?", style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    text = "This will permanently remove '${currentItem.title}' from your saved collection.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            dao.deleteItem(currentItem)
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeleteContent)
                ) { Text("Delete", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun MetadataStatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LaterlySurfaceContainerHigh)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionDockButton(
    label: String,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isDestructive) DeleteContainer else LaterlySurfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isDestructive) DeleteContent else LaterlyPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isDestructive) DeleteContent else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

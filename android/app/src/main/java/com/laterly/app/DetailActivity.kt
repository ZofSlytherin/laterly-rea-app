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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.laterly.app.ui.theme.LaterlyTheme
import com.laterly.app.ui.theme.NdAccent
import com.laterly.app.ui.theme.NdBorder
import com.laterly.app.ui.theme.NdTextDisabled
import com.laterly.app.ui.theme.NdTextSecondary
import com.laterly.app.ui.theme.SpaceGrotesk
import com.laterly.app.ui.theme.SpaceMono
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
                text = "[LOADING...]",
                fontFamily = SpaceMono,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = NdTextDisabled
            )
        }
        return
    }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val formattedDate = remember(currentItem.createdAt) { dateFormat.format(Date(currentItem.createdAt)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == 0) "OVERVIEW" else "READER",
                        fontFamily = SpaceMono,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = NdTextSecondary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    // Favorite toggle — accent red when active is the ONE accent moment for this screen
                    IconButton(onClick = {
                        val updated = currentItem.copy(isFavorite = !currentItem.isFavorite)
                        item = updated
                        coroutineScope.launch { dao.updateItem(updated) }
                    }) {
                        Icon(
                            imageVector = if (currentItem.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (currentItem.isFavorite) NdAccent else NdTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Action dock — flat bar, hairline top border, 4 icon-label items
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(NdBorder)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionDockButton("Edit",   Icons.Default.Edit,          false) { showEditNotesDialog = true }
                    ActionDockButton("Move",   Icons.Default.Folder,         false) { showMoveDialog = true }
                    ActionDockButton("Share",  Icons.Default.Share,          false) {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${currentItem.title}\n${currentItem.url}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Link"))
                    }
                    ActionDockButton("Delete", Icons.Default.Delete,         true)  { showDeleteConfirmDialog = true }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab selector — hairline underline, Space Mono ALL CAPS
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                NdTab("OVERVIEW", selectedTab == 0) { selectedTab = 0 }
                NdTab(
                    label = if (currentItem.articleContent.isNotBlank()) "READER" else "READER (N/A)",
                    isSelected = selectedTab == 1
                ) { selectedTab = 1 }
            }
            // Tab bottom hairline
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            if (selectedTab == 0) {
                // ── OVERVIEW TAB ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Hero image — full width, 200dp, technical square corners
                    if (!currentItem.imageUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            AsyncImage(
                                model = currentItem.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Collection label — top-left, flat white chip
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentItem.collectionName.uppercase(),
                                    fontFamily = SpaceMono,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // Title — primary layer, Space Grotesk Medium
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val domain = Uri.parse(currentItem.url).host ?: ""
                        Text(
                            text = domain.uppercase(),
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            color = NdTextDisabled
                        )
                        Text(
                            text = currentItem.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Open source row — hairline bordered, flat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.url))
                                context.startActivity(browserIntent)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            tint = NdTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Visit original source",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${currentItem.estimatedReadMinutes} min read",
                                fontFamily = SpaceMono,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                color = NdTextDisabled
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = NdTextDisabled,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Metadata stat row — 2×2, Space Mono data, no pill backgrounds
                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        Text(
                            text = "METADATA",
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = NdTextDisabled
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            MetadataStatTile(
                                modifier = Modifier.weight(1f),
                                label = "ADDED",
                                value = formattedDate
                            )
                            MetadataStatTile(
                                modifier = Modifier.weight(1f),
                                label = "READ TIME",
                                value = "${currentItem.estimatedReadMinutes} min"
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            MetadataStatTile(
                                modifier = Modifier.weight(1f),
                                label = "SOURCE",
                                value = currentItem.author.ifBlank { "Web" }
                            )
                            MetadataStatTile(
                                modifier = Modifier.weight(1f),
                                label = "WORDS",
                                value = if (currentItem.wordCount > 0) "~${currentItem.wordCount}" else "N/A"
                            )
                        }
                    }

                    // Personal notes
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NOTES",
                                fontFamily = SpaceMono,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = NdTextDisabled
                            )
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NdAccent, // accent on the action — the ONE red on this card
                                modifier = Modifier.clickable { showEditNotesDialog = true }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (currentItem.personalNotes.isNotBlank())
                                    currentItem.personalNotes
                                else
                                    "No notes yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (currentItem.personalNotes.isNotBlank())
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    NdTextDisabled
                            )
                        }
                    }

                    // Assigned collection
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "COLLECTION",
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = NdTextDisabled
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { showMoveDialog = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = NdTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = currentItem.collectionName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Change",
                                fontFamily = SpaceMono,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                color = NdTextDisabled
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                // ── READER TAB ────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = currentItem.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildString {
                            if (currentItem.author.isNotBlank()) append("${currentItem.author.uppercase()} · ")
                            append("${currentItem.estimatedReadMinutes} MIN")
                        },
                        fontFamily = SpaceMono,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp,
                        color = NdTextDisabled
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (currentItem.articleContent.isNotBlank()) {
                        Text(
                            text = currentItem.articleContent,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Text(
                            text = "[READER UNAVAILABLE]",
                            fontFamily = SpaceMono,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = NdTextDisabled
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Open the original source to read this article",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NdTextSecondary
                        )
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
            title = {
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                OutlinedTextField(
                    value = noteDraft,
                    onValueChange = { noteDraft = it },
                    placeholder = { Text("Add takeaways or quotes…") },
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
                    colors = ButtonDefaults.buttonColors(containerColor = NdAccent)
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
            title = { Text("Move to", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    collections.forEach { col ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val updated = currentItem.copy(collectionName = col)
                                    item = updated
                                    coroutineScope.launch { dao.updateItem(updated) }
                                    showMoveDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = col,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (currentItem.collectionName == col)
                                    MaterialTheme.colorScheme.onBackground
                                else NdTextSecondary,
                                fontWeight = if (currentItem.collectionName == col) FontWeight.Medium else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (currentItem.collectionName == col) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(NdAccent, RoundedCornerShape(3.dp))
                                )
                            }
                        }
                        // hairline divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
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
            title = { Text("Delete save?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    text = "This permanently removes '${currentItem.title}'",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NdTextSecondary
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
                    colors = ButtonDefaults.buttonColors(containerColor = NdAccent)
                ) { Text("Delete", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

/** Nothing-style tab: flat text, hairline active underline in accent red */
@Composable
fun NdTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontFamily = SpaceMono,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            color = if (isSelected) MaterialTheme.colorScheme.onBackground else NdTextSecondary
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(
                    if (isSelected) NdAccent else Color.Transparent
                )
        )
    }
}

/**
 * Metadata stat tile — flat surface, Space Mono data value, label tertiary.
 * No pill, no background bubble — spacing is the container.
 */
@Composable
fun MetadataStatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontFamily = SpaceMono,
            fontSize = 9.sp,
            letterSpacing = 0.8.sp,
            color = NdTextDisabled
        )
        Text(
            text = value,
            fontFamily = SpaceMono,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Action dock button — flat, icon + Space Mono label below.
 * Delete uses accent red — the ONE destructive signal on the screen.
 */
@Composable
fun ActionDockButton(
    label: String,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isDestructive) NdAccent else NdTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label.uppercase(),
            fontFamily = SpaceMono,
            fontSize = 9.sp,
            letterSpacing = 0.8.sp,
            color = if (isDestructive) NdAccent else NdTextDisabled
        )
    }
}

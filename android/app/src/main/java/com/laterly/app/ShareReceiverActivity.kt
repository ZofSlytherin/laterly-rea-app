package com.laterly.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.laterly.app.data.AppDatabase
import com.laterly.app.data.LinkMetadata
import com.laterly.app.data.MetadataExtractor
import com.laterly.app.data.SavedItem
import com.laterly.app.ui.theme.LaterlyPrimary
import com.laterly.app.ui.theme.LaterlySurfaceContainer
import com.laterly.app.ui.theme.LaterlySurfaceContainerHigh
import com.laterly.app.ui.theme.LaterlyTheme
import kotlinx.coroutines.launch

/**
 * Bottom-sheet style modal activity for ACTION_SEND.
 * Allows quick confirmation, collection assignment, and tag addition before saving.
 */
class ShareReceiverActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var initialText = ""
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            initialText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        }

        if (initialText.isBlank()) {
            Toast.makeText(this, "No valid link found to save", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.savedItemDao()

        setContent {
            LaterlyTheme {
                ShareSaveSheet(
                    rawText = initialText,
                    onSave = { item ->
                        lifecycleScope.launch {
                            dao.insertItem(item)
                            Toast.makeText(applicationContext, "Saved to ${item.collectionName}", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    },
                    onDismiss = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShareSaveSheet(
    rawText: String,
    onSave: (SavedItem) -> Unit,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var metadata by remember { mutableStateOf<LinkMetadata?>(null) }
    var selectedCollection by remember { mutableStateOf("Inbox") }
    var customTags by remember { mutableStateOf("") }
    val defaultCollections = listOf("Inbox", "Recipes", "Reading", "Travel", "Tech", "Inspiration")

    LaunchedEffect(rawText) {
        val extracted = MetadataExtractor.extract(rawText)
        metadata = extracted
        isLoading = false
    }

    // Modal background overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(enabled = false) {}, // prevent dismissing when clicking content
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Save to Laterly",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LaterlyPrimary)
                    }
                } else {
                    val meta = metadata
                    if (meta != null) {
                        // Preview Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LaterlySurfaceContainer, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!meta.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = meta.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = meta.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = meta.url,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Collection Selector
                        Text(
                            text = "Choose Collection",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            defaultCollections.forEach { col ->
                                val isSelected = selectedCollection == col
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) LaterlyPrimary else LaterlySurfaceContainerHigh)
                                        .clickable { selectedCollection = col }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = col,
                                        color = if (isSelected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tags input
                        OutlinedTextField(
                            value = customTags,
                            onValueChange = { customTags = it },
                            label = { Text("Tags (comma-separated)") },
                            placeholder = { Text("e.g. read-later, design, swift") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save Button
                        Button(
                            onClick = {
                                onSave(
                                    SavedItem(
                                        url = meta.url,
                                        title = meta.title,
                                        description = meta.description,
                                        imageUrl = meta.imageUrl,
                                        collectionName = selectedCollection,
                                        tags = customTags.split(",").map { it.trim() }.filter { it.isNotBlank() }.joinToString(","),
                                        articleContent = meta.articleContent,
                                        author = meta.author,
                                        estimatedReadMinutes = meta.estimatedReadMinutes,
                                        wordCount = meta.wordCount
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LaterlyPrimary),
                            shape = CircleShape
                        ) {
                            Text("Save Item", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

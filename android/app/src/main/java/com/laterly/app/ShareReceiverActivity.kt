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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
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
import com.laterly.app.ui.theme.LaterlyTheme
import com.laterly.app.ui.theme.NdAccent
import com.laterly.app.ui.theme.NdBorder
import com.laterly.app.ui.theme.NdTextDisabled
import com.laterly.app.ui.theme.NdTextSecondary
import com.laterly.app.ui.theme.SpaceGrotesk
import com.laterly.app.ui.theme.SpaceMono
import kotlinx.coroutines.launch

/**
 * Bottom-sheet modal for ACTION_SEND intents.
 * Nothing-style: flat surfaces, no decorative shadows, Space Mono labels.
 */
class ShareReceiverActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var initialText = ""
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            initialText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        }

        if (initialText.isBlank()) {
            Toast.makeText(this, "No valid link to save", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(applicationContext, "[SAVED] ${item.collectionName}", Toast.LENGTH_SHORT).show()
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
        metadata = MetadataExtractor.extract(rawText)
        isLoading = false
    }

    // Scrim overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)) // flat, no rounding
                .clickable(enabled = false) {},
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Handle bar — minimal visual cue
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 2.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SAVE TO LATERLY",
                        fontFamily = SpaceMono,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NdTextSecondary,
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Nothing: text status instead of spinner when possible, but spinner is functional
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = NdAccent,
                                strokeWidth = 1.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "[FETCHING...]",
                                fontFamily = SpaceMono,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = NdTextDisabled
                            )
                        }
                    }
                } else {
                    val meta = metadata
                    if (meta != null) {
                        // Link preview — flat, hairline border
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!meta.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = meta.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = meta.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = meta.url,
                                    fontFamily = SpaceMono,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.3.sp,
                                    color = NdTextDisabled,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Collection label
                        Text(
                            text = "COLLECTION",
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = NdTextDisabled
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Collection chips — flat, Space Mono
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            defaultCollections.forEach { col ->
                                val isSelected = selectedCollection == col
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.onBackground
                                            else MaterialTheme.colorScheme.surfaceContainerHigh
                                        )
                                        .clickable { selectedCollection = col }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = col.uppercase(),
                                        fontFamily = SpaceMono,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.8.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.background else NdTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tags input
                        Text(
                            text = "TAGS",
                            fontFamily = SpaceMono,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = NdTextDisabled
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customTags,
                            onValueChange = { customTags = it },
                            placeholder = {
                                Text(
                                    text = "read-later, design, swift",
                                    fontFamily = SpaceMono,
                                    fontSize = 11.sp,
                                    color = NdTextDisabled
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Save button — accent red, full width
                        Button(
                            onClick = {
                                onSave(
                                    SavedItem(
                                        url = meta.url,
                                        title = meta.title,
                                        description = meta.description,
                                        imageUrl = meta.imageUrl,
                                        collectionName = selectedCollection,
                                        tags = customTags.split(",")
                                            .map { it.trim() }
                                            .filter { it.isNotBlank() }
                                            .joinToString(","),
                                        articleContent = meta.articleContent,
                                        author = meta.author,
                                        estimatedReadMinutes = meta.estimatedReadMinutes,
                                        wordCount = meta.wordCount
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NdAccent),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Save",
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

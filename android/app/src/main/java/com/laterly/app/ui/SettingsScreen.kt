package com.laterly.app.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laterly.app.ui.theme.NdAccent
import com.laterly.app.ui.theme.NdBorder
import com.laterly.app.ui.theme.NdTextDisabled
import com.laterly.app.ui.theme.NdTextSecondary
import com.laterly.app.ui.theme.SpaceGrotesk
import com.laterly.app.ui.theme.SpaceMono

/**
 * Settings screen — Nothing design system.
 * Flat grouped rows with hairline borders. No cards or shadows.
 */
@Composable
fun SettingsScreen() {
    var offlineSync by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Storage ──────────────────────────────────────────────────────────
        SettingsSectionLabel("STORAGE")
        SettingsGroup {
            SettingsRow(
                label = "Local storage",
                value = "SQLite · On-device only"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Appearance ───────────────────────────────────────────────────────
        SettingsSectionLabel("APPEARANCE")
        SettingsGroup {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Follows system setting",
                    fontFamily = SpaceMono,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    color = NdTextDisabled
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Connectivity ─────────────────────────────────────────────────────
        SettingsSectionLabel("CONNECTIVITY")
        SettingsGroup {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Offline mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Save and read without internet",
                        fontFamily = SpaceMono,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = NdTextDisabled
                    )
                }
                Switch(
                    checked = offlineSync,
                    onCheckedChange = { offlineSync = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.background,
                        checkedTrackColor = NdAccent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── About ─────────────────────────────────────────────────────────────
        SettingsSectionLabel("ABOUT")
        SettingsGroup {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Laterly for Android",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1.0.0-mvp · Open Source · Local-first",
                    fontFamily = SpaceMono,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    color = NdTextDisabled
                )
            }
        }
    }
}

/** Space Mono ALL CAPS section label */
@Composable
private fun SettingsSectionLabel(label: String) {
    Text(
        text = label,
        fontFamily = SpaceMono,
        fontSize = 10.sp,
        letterSpacing = 1.sp,
        color = NdTextDisabled,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

/** Flat bordered group container — hairline border, no shadow */
@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(4.dp))
    ) {
        content()
    }
}

/** Single-row setting with label + value pair */
@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            fontFamily = SpaceMono,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            color = NdTextDisabled
        )
    }
}

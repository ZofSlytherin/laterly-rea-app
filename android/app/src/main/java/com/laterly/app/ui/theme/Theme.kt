package com.laterly.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.laterly.app.R

// ─── Font Families ───────────────────────────────────────────────────────────
// Space Grotesk: body / UI text — same foundry as Nothing's typefaces
val SpaceGrotesk = FontFamily(Font(R.font.space_grotesk))

// Space Mono: data, labels, timestamps — instrument-panel monospace
val SpaceMono = FontFamily(Font(R.font.space_mono))

// ─── Nothing Design System: Color Tokens ─────────────────────────────────────
// Dark (OLED)
val NdBlack        = Color(0xFF000000)
val NdSurface      = Color(0xFF111111) // Elevated surfaces, cards
val NdSurfaceRaised= Color(0xFF1A1A1A) // Secondary elevation
val NdBorder       = Color(0xFF222222) // Subtle dividers
val NdBorderVisible= Color(0xFF333333) // Intentional borders
val NdTextDisabled = Color(0xFF666666) // Disabled / decorative elements
val NdTextSecondary= Color(0xFF999999) // Labels, captions, metadata
val NdTextPrimary  = Color(0xFFE8E8E8) // Body text
val NdTextDisplay  = Color(0xFFFFFFFF) // Headlines, hero numbers

// Light (warm off-white printed manual)
val NdLightBg      = Color(0xFFF5F5F5) // Page background
val NdLightSurface = Color(0xFFFFFFFF) // Cards
val NdLightSurfaceRaised = Color(0xFFF0F0F0) // Secondary elevation
val NdLightBorder  = Color(0xFFE8E8E8) // Subtle dividers
val NdLightBorderVisible = Color(0xFFCCCCCC) // Intentional borders
val NdLightTextDisabled  = Color(0xFF999999) // Disabled
val NdLightTextSecondary = Color(0xFF666666) // Labels, captions
val NdLightTextPrimary   = Color(0xFF1A1A1A) // Body text
val NdLightTextDisplay   = Color(0xFF000000) // Headlines

// Accent & status — same in both modes
val NdAccent       = Color(0xFFD71921) // Signal red — one per screen, never decorative
val NdAccentSubtle = Color(0x26D71921) // rgba(215,25,33,0.15)
val NdSuccess      = Color(0xFF4A9E5C) // Confirmed / completed
val NdWarning      = Color(0xFFD4A843) // Caution / pending
val NdInteractiveDark  = Color(0xFF5B9BF6) // Tappable text in dark
val NdInteractiveLight = Color(0xFF007AFF) // Tappable text in light

// ─── Color Schemes ────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = NdAccent,
    onPrimary          = NdTextDisplay,
    primaryContainer   = NdSurfaceRaised,
    onPrimaryContainer = NdTextPrimary,
    secondary          = NdTextSecondary,
    onSecondary        = NdBlack,
    secondaryContainer = NdSurfaceRaised,
    onSecondaryContainer = NdTextPrimary,
    background         = NdBlack,
    onBackground       = NdTextPrimary,
    surface            = NdSurface,
    onSurface          = NdTextPrimary,
    surfaceVariant     = NdSurfaceRaised,
    onSurfaceVariant   = NdTextSecondary,
    surfaceContainerLow     = NdSurface,
    surfaceContainer        = NdSurface,
    surfaceContainerHigh    = NdSurfaceRaised,
    surfaceContainerHighest = NdSurfaceRaised,
    outline            = NdBorderVisible,
    outlineVariant     = NdBorder,
    error              = NdAccent,
    onError            = NdTextDisplay,
    scrim              = Color(0xCC000000)
)

private val LightColorScheme = lightColorScheme(
    primary            = NdAccent,
    onPrimary          = NdTextDisplay,
    primaryContainer   = NdLightSurfaceRaised,
    onPrimaryContainer = NdLightTextPrimary,
    secondary          = NdLightTextSecondary,
    onSecondary        = NdTextDisplay,
    secondaryContainer = NdLightSurfaceRaised,
    onSecondaryContainer = NdLightTextPrimary,
    background         = NdLightBg,
    onBackground       = NdLightTextPrimary,
    surface            = NdLightSurface,
    onSurface          = NdLightTextPrimary,
    surfaceVariant     = NdLightSurfaceRaised,
    onSurfaceVariant   = NdLightTextSecondary,
    surfaceContainerLow     = NdLightSurface,
    surfaceContainer        = NdLightSurface,
    surfaceContainerHigh    = NdLightSurfaceRaised,
    surfaceContainerHighest = NdLightSurfaceRaised,
    outline            = NdLightBorderVisible,
    outlineVariant     = NdLightBorder,
    error              = NdAccent,
    onError            = NdTextDisplay,
    scrim              = Color(0x66000000)
)

// ─── Typography ────────────────────────────────────────────────────────────────
// Nothing rule: Space Grotesk for UI/body, Space Mono for labels/data.
// 3 sizes max per screen, 2 weights (400 + 500), 600 rare.
// Labels: Space Mono ALL CAPS at 11–12sp with 0.08–0.1em letter-spacing.
val LaterlyTypography = Typography(
    // Display — hero moments only (Doto would go here but no Doto yet)
    headlineLarge = TextStyle(
        fontFamily    = SpaceGrotesk,
        fontWeight    = FontWeight.Medium,
        fontSize      = 32.sp,
        lineHeight    = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    // Screen / section headings
    headlineSmall = TextStyle(
        fontFamily    = SpaceGrotesk,
        fontWeight    = FontWeight.Medium,
        fontSize      = 22.sp,
        lineHeight    = 28.sp,
        letterSpacing = (-0.25).sp
    ),
    // Card titles, content titles
    titleLarge = TextStyle(
        fontFamily    = SpaceGrotesk,
        fontWeight    = FontWeight.Medium,
        fontSize      = 18.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily    = SpaceGrotesk,
        fontWeight    = FontWeight.Medium,
        fontSize      = 15.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily    = SpaceGrotesk,
        fontWeight    = FontWeight.Normal,
        fontSize      = 13.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.sp
    ),
    // Body content — reader view, descriptions
    bodyLarge = TextStyle(
        fontFamily    = SpaceGrotesk,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 26.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily    = SpaceGrotesk,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily    = SpaceMono,
        fontWeight    = FontWeight.Normal,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Space Mono: ALL CAPS metadata labels (domain, timestamps, counts)
    labelMedium = TextStyle(
        fontFamily    = SpaceMono,
        fontWeight    = FontWeight.Normal,
        fontSize      = 11.sp,
        lineHeight    = 14.sp,
        letterSpacing = 0.9.sp   // ~0.08em at 11sp
    ),
    labelSmall = TextStyle(
        fontFamily    = SpaceMono,
        fontWeight    = FontWeight.Normal,
        fontSize      = 10.sp,
        lineHeight    = 12.sp,
        letterSpacing = 1.0.sp
    )
)

// ─── Theme Composable ─────────────────────────────────────────────────────────
@Composable
fun LaterlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = LaterlyTypography,
        content     = content
    )
}

package com.laterly.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Color Tokens from artifacts/design.md
val LaterlyPrimary = Color(0xFF7A2B02) // Deep terracotta / rust brown
val LaterlyPrimaryContainer = Color(0xFF9C4215)
val LaterlySecondary = Color(0xFFF7931E) // Bright amber / marigold
val LaterlySecondaryContainer = Color(0xFFFAE3D2)

val LaterlySurface = Color(0xFFFCF8F6)
val LaterlySurfaceContainerLow = Color(0xFFF7F3F1)
val LaterlySurfaceContainer = Color(0xFFF4EEEA)
val LaterlySurfaceContainerHigh = Color(0xFFEDE6E1)
val LaterlySurfaceContainerHighest = Color(0xFFE5E0DB)

val LaterlyOnSurface = Color(0xFF1C1B1A)
val LaterlyOnSurfaceVariant = Color(0xFF6B5851)
val LaterlyOutline = Color(0xFF897269)
val LaterlyOutlineVariant = Color(0xFFDCC1B6)

val BadgeArticle = Color(0xFFF39200)
val BadgeRecipe = Color(0xFFFAE8DC)
val BadgeVideo = Color(0xFFF7DED5)
val DeleteContainer = Color(0xFFFCDCD9)
val DeleteContent = Color(0xFFD32F2F)

private val LightColorScheme = lightColorScheme(
    primary = LaterlyPrimary,
    onPrimary = Color.White,
    primaryContainer = LaterlyPrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = LaterlySecondary,
    onSecondary = Color.White,
    secondaryContainer = LaterlySecondaryContainer,
    onSecondaryContainer = LaterlyPrimary,
    surface = LaterlySurface,
    onSurface = LaterlyOnSurface,
    surfaceVariant = LaterlySurfaceContainer,
    onSurfaceVariant = LaterlyOnSurfaceVariant,
    surfaceContainerLow = LaterlySurfaceContainerLow,
    surfaceContainer = LaterlySurfaceContainer,
    surfaceContainerHigh = LaterlySurfaceContainerHigh,
    surfaceContainerHighest = LaterlySurfaceContainerHighest,
    outline = LaterlyOutline,
    outlineVariant = LaterlyOutlineVariant,
    background = LaterlySurface,
    onBackground = LaterlyOnSurface
)

val LaterlyTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.02).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

@Composable
fun LaterlyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = LaterlyTypography,
        content = content
    )
}

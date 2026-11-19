package com.eneque.campusgps.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import com.eneque.campusgps.R

// Fuentes empaquetadas: no dependen de Internet ni de Google Play Services.
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val CampusSans = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.dm_sans, FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.dm_sans, FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700)))
)
val CampusMono = FontFamily(
    Font(R.font.dm_mono_regular, FontWeight.Normal),
    Font(R.font.dm_mono_medium, FontWeight.Medium)
)

val Typography = Typography(
    headlineSmall = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 19.sp),
    titleSmall = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp),
    bodyLarge = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 19.sp),
    labelMedium = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = CampusSans, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp)
)

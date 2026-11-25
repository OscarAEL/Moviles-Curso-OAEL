package com.eneque.murocampus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.sp
import com.eneque.murocampus.R

val ArchivoBlack = FontFamily(Font(R.font.archivo_black_regular, FontWeight.Black))
@OptIn(ExperimentalTextApi::class)
val DmSans = FontFamily(
    Font(R.font.dm_sans_variable, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.dm_sans_variable, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700)))
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = ArchivoBlack, fontWeight = FontWeight.Black, fontSize = 44.sp, lineHeight = 46.sp, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = ArchivoBlack, fontWeight = FontWeight.Black, fontSize = 30.sp, lineHeight = 35.sp, letterSpacing = (-0.6).sp),
    headlineMedium = TextStyle(fontFamily = ArchivoBlack, fontWeight = FontWeight.Black, fontSize = 25.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = ArchivoBlack, fontWeight = FontWeight.Black, fontSize = 21.sp, lineHeight = 27.sp),
    titleMedium = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 23.sp),
    bodyLarge = TextStyle(fontFamily = DmSans, fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontFamily = DmSans, fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 21.sp),
    labelMedium = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp)
)

@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.tecsup.lavacampus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.*
import com.tecsup.lavacampus.R

private val Jakarta =
    FontFamily(
        listOf(
                FontWeight.Normal,
                FontWeight.Medium,
                FontWeight.SemiBold,
                FontWeight.Bold,
                FontWeight.ExtraBold,
            )
            .map { weight ->
                Font(
                    R.font.plus_jakarta_sans,
                    weight = weight,
                    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
                )
            }
    )
val Typography =
    Typography().let { base ->
        base.copy(
            displayLarge = base.displayLarge.copy(fontFamily = Jakarta),
            displayMedium = base.displayMedium.copy(fontFamily = Jakarta),
            displaySmall = base.displaySmall.copy(fontFamily = Jakarta),
            headlineLarge = base.headlineLarge.copy(fontFamily = Jakarta),
            headlineMedium = base.headlineMedium.copy(fontFamily = Jakarta),
            headlineSmall = base.headlineSmall.copy(fontFamily = Jakarta),
            titleLarge = base.titleLarge.copy(fontFamily = Jakarta),
            titleMedium = base.titleMedium.copy(fontFamily = Jakarta),
            titleSmall = base.titleSmall.copy(fontFamily = Jakarta),
            bodyLarge = base.bodyLarge.copy(fontFamily = Jakarta),
            bodyMedium = base.bodyMedium.copy(fontFamily = Jakarta),
            bodySmall = base.bodySmall.copy(fontFamily = Jakarta),
            labelLarge = base.labelLarge.copy(fontFamily = Jakarta),
            labelMedium = base.labelMedium.copy(fontFamily = Jakarta),
            labelSmall = base.labelSmall.copy(fontFamily = Jakarta),
        )
    }

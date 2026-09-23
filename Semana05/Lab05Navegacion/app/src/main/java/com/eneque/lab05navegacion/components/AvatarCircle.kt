package com.eneque.lab05navegacion.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05navegacion.ui.theme.AcademicLilaAccent
import com.eneque.lab05navegacion.ui.theme.AcademicPurpleDark
import com.eneque.lab05navegacion.ui.theme.AcademicPurplePrimary

@Composable
fun AvatarCircle(
    modifier: Modifier = Modifier,
    initials: String = "",
    @DrawableRes imageRes: Int? = null,
    size: Dp = 50.dp,
    borderColor: Color = Color.White,
    borderWidth: Dp = 2.dp,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(AcademicPurplePrimary, AcademicPurpleDark),
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(gradient)
            .border(borderWidth, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if ((imageRes != null) && (imageRes != 0)) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Avatar de usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
            )
        } else if (initials.isNotBlank()) {
            Text(
                text = initials.take(2).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp,
                style = MaterialTheme.typography.titleMedium,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = AcademicLilaAccent,
                modifier = Modifier.size(size * 0.6f),
            )
        }
    }
}

package com.eneque.lab05tecsupfit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05tecsupfit.navigation.Screen
import com.eneque.lab05tecsupfit.ui.theme.GreenDarkHeader
import com.eneque.lab05tecsupfit.ui.theme.TextSecondary

data class BottomNavItem(
    val title: String,
    val route: String
)

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Inicio", Screen.Home.route),
        BottomNavItem("Reservas", Screen.Reservas.route),
        BottomNavItem("Rutinas", Screen.Rutinas.route),
        BottomNavItem("Perfil", Screen.Perfil.route)
    )

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = (currentRoute == item.route)
                val activeColor = GreenDarkHeader
                val inactiveColor = TextSecondary

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(item.route) }
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    // Círculo pequeño simple en la parte superior
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.White, CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) activeColor else inactiveColor,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    AppBottomBar(
        currentRoute = Screen.Home.route,
        onTabSelected = {}
    )
}

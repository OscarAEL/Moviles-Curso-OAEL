package com.eneque.lab05navegacion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eneque.lab05navegacion.R
import com.eneque.lab05navegacion.components.AvatarCircle
import com.eneque.lab05navegacion.data.StudentRepository
import com.eneque.lab05navegacion.navigation.Screen
import com.eneque.lab05navegacion.ui.theme.AcademicBackground
import com.eneque.lab05navegacion.ui.theme.AcademicLilaContainer
import com.eneque.lab05navegacion.ui.theme.AcademicPurpleDark
import com.eneque.lab05navegacion.ui.theme.AcademicPurplePrimary
import com.eneque.lab05navegacion.ui.theme.AcademicTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val user = StudentRepository.currentUser
    val headerGradient = Brush.verticalGradient(
        colors = listOf(AcademicPurpleDark, AcademicPurplePrimary),
    )

    val softRedBg = Color(0xFFFFEBEE)
    val softRedText = Color(0xFFD32F2F)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración de Perfil",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = AcademicPurplePrimary,
                    navigationIconContentColor = AcademicPurplePrimary,
                ),
            )
        },
        containerColor = AcademicBackground,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header Area (Photo and Name strictly INSIDE the Purple Area)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = headerGradient,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    )
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AvatarCircle(
                        imageRes = R.drawable.oscar_eneque,
                        initials = user.initials,
                        size = 80.dp,
                        borderColor = Color.White,
                        borderWidth = 3.dp,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Oscar Eneque Lluen",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Content Area with weight spacer pushing Cerrar Sesión button to the bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                // Section 1: INFORMACIÓN PERSONAL
                Text(
                    text = "INFORMACIÓN PERSONAL",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AcademicPurplePrimary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                ProfileSimpleRow(
                    label = "Nombre Completo",
                    value = "Oscar Eneque Lluen",
                    icon = Icons.Default.Person,
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                )

                ProfileSimpleRow(
                    label = "Correo",
                    value = "oscar.eneque.l@tecsup.edu.pe",
                    icon = Icons.Default.Email,
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                )

                ProfileSimpleRow(
                    label = "Teléfono",
                    value = "+51 987 654 321",
                    icon = Icons.Default.Phone,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: ACADÉMICO
                Text(
                    text = "ACADÉMICO",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AcademicPurplePrimary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                ProfileSimpleRow(
                    label = "Carrera",
                    value = "Diseño y Desarrollo de Software",
                    icon = Icons.Default.School,
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                )

                ProfileSimpleRow(
                    label = "Ciclo Actual",
                    value = "IV Ciclo",
                    icon = Icons.Default.Class,
                )

                // Large vertical spacing pushing Cerrar Sesión to the bottom area
                Spacer(modifier = Modifier.height(56.dp))

                // Cerrar Sesión Button (Soft Red Background & Red Text/Icon)
                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = softRedBg,
                        contentColor = softRedText,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar Sesión",
                        tint = softRedText,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = softRedText,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileSimpleRow(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(AcademicLilaContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AcademicPurplePrimary,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AcademicTextSecondary,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

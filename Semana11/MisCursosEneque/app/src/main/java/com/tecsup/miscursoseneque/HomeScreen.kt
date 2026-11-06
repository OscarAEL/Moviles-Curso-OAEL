package com.tecsup.miscursoseneque

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    userEmail: String,
    onSignOut: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val activeEmail = userEmail.ifEmpty { auth.currentUser?.email ?: "" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HeaderGradientBrush)
    ) {
        // Main screen content with system bar padding handling
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                TopHeaderRow()

                // Central Academic Graphic Illustration (Graduation Cap, Concentric Rings, Floating Wireframes)
                AcademicHeaderIllustration(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // White Panel containing welcome message, user email & action button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title inside white panel
                    Text(
                        text = "¡Bienvenido/a!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumColors.TextDark,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tu sesión está activa. Ya puedes comenzar tu experiencia académica.",
                        fontSize = 14.sp,
                        color = PremiumColors.TextMuted,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Email display box showing the actual authenticated user's email
                    EmailDisplayBox(
                        email = activeEmail
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Sign Out Button
                    GradientButton(
                        text = "Cerrar sesión",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        onClick = {
                            auth.signOut()
                            onSignOut()
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f, fill = false).heightIn(min = 36.dp))

                    // Footer text at bottom
                    AppFooter()
                }
            }
        }
    }
}

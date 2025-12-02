package com.example.explorelocal.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.navigation.AppNavigation
import com.example.explorelocal.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import com.example.explorelocal.R
import com.example.explorelocal.ui.theme.PrimaryPurple
import com.example.explorelocal.ui.theme.SecondaryPurple

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    var showButton by remember { mutableStateOf(false) }

    // Check login status
    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn(context)
        delay(2000)
        showButton = true
    }

    // Auto navigate jika sudah login
    LaunchedEffect(userState) {
        when(userState) {
            is UserState.LoggedIn -> {
                delay(500)
                navController.navigate("umkm_list") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ==========================================
        // KOLOM ATAS - Logo & App Name (Purple)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.1f)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 40.dp,
                        bottomEnd = 40.dp
                    )
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PrimaryPurple,
                            SecondaryPurple
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                 Image(
                     painter = painterResource(R.drawable.logo),
                     contentDescription = "ExploreLocal Logo",
                     modifier = Modifier.size(180.dp)
                 )

                Spacer(modifier = Modifier.height(24.dp))

                // App Name
                Text(
                    text = "ExploreLocal",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ==========================================
        // KOLOM BAWAH - Tagline & Button (White)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
                .background(Color.White)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tagline
            Text(
                text = "Ayo Jelajahi UMKM Sekitar!",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryPurple,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Button Mulai
            if (showButton || userState is UserState.LoggedOut || userState is UserState.Error) {
                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D1B4E)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Mulai",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


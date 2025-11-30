package com.example.explorelocal.ui.screen.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.data.repository.SupabaseAuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SupabaseAuthViewModel
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        when(userState) {
            is UserState.Success -> {
                // User sudah login, navigate ke home
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is UserState.Error -> {
                // User belum login, navigate ke login
                delay(1000) // Splash delay
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> { /* Loading */ }
        }
    }

    // UI Splash
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ExploreLocal",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

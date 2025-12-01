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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn(context)
    }

    LaunchedEffect(userState) {
        when(userState) {
            is UserState.LoggedIn -> {
                delay(1000)
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is UserState.LoggedOut, is UserState.Error -> {
                delay(1000)
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> { /* Loading - tetap di splash */ }

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
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Jelajahi UMKM Sekitar",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}

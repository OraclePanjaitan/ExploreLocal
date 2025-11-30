package com.example.explorelocal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.explorelocal.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) "home" else "login"
    ) {
        composable("splash") { SplashContent(authViewModel) }
        composable("login") { LoginScreen(authViewModel, navController) }
        composable("register") { RegisterScreen(authViewModel, navController) }
        composable("home") { HomeScreen(navController) }
        composable("umkm_list") { UmkmListScreen(navController) }
        composable("umkm_detail/{umkmId}") {
            UmkmDetailScreen(navController)
        }
        // ... route lainnya
    }
}

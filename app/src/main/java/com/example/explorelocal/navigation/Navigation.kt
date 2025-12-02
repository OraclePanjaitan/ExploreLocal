package com.example.explorelocal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.ui.screen.auth.LoginScreen
import com.example.explorelocal.ui.screen.auth.RegisterScreen
import com.example.explorelocal.ui.screen.auth.SplashScreen
import com.example.explorelocal.ui.screen.auth.SplashScreen
import com.example.explorelocal.ui.screen.location.UmkmLocationScreen
import com.example.explorelocal.ui.screen.profile.ProfileScreen
import com.example.explorelocal.ui.screen.promo.PromoListScreen
import com.example.explorelocal.ui.screen.umkm.UmkmListScreen
import com.example.explorelocal.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController, authViewModel)
        }
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("register") {
            RegisterScreen(navController, authViewModel)
        }
        composable("umkm_list") {
            UmkmListScreen(navController)
        }
        composable("umkm_list") {
            UmkmListScreen(navController)
        }
        composable("umkm_detail/{umkmId}") { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getString("umkmId")
            // UmkmDetailScreen(navController, umkmId)
        }
        composable("promo") {
            PromoListScreen(navController)
        }
        composable("location") {
            UmkmLocationScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController, authViewModel)
        }
    }
}

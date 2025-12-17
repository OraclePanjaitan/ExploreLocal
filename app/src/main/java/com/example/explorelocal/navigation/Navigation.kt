package com.example.explorelocal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.explorelocal.ui.screen.auth.LoginScreen
import com.example.explorelocal.ui.screen.auth.RegisterScreen
import com.example.explorelocal.ui.screen.auth.SplashScreen
import com.example.explorelocal.ui.screen.OnboardingRole.Onboarding1
import com.example.explorelocal.ui.screen.OnboardingRole.Onboarding2
import com.example.explorelocal.ui.screen.OnboardingRole.Onboarding3
import com.example.explorelocal.ui.screen.OnboardingRole.OnboardingRole
import com.example.explorelocal.ui.screen.location.UmkmLocationScreen
import com.example.explorelocal.ui.screen.profile.ProfileScreen
import com.example.explorelocal.ui.screen.promo.AddPromoScreen
import com.example.explorelocal.ui.screen.promo.PromoListScreen
import com.example.explorelocal.ui.screen.umkm.UmkmListScreen
import com.example.explorelocal.viewmodel.AuthViewModel
import com.example.explorelocal.viewmodel.UmkmState
import com.example.explorelocal.viewmodel.UmkmViewModel

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

        composable("onboarding1") {
            Onboarding1(navController)
        }

        composable("onboarding2") {
            Onboarding2(navController)
        }

        composable("onboarding3") {
            Onboarding3(navController)
        }

        composable("role") {
            OnboardingRole(navController)
        }

        composable("login?role={role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            LoginScreen(navController, authViewModel, role)
        }
        composable("register?role={role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            RegisterScreen(navController, authViewModel, role)
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
        composable("add_promo") {
            val umkmViewModel: UmkmViewModel = viewModel()
            val umkmState by umkmViewModel.umkmState.collectAsState()
            val umkmList = if (umkmState is UmkmState.UmkmList) {
                (umkmState as UmkmState.UmkmList).data
            } else {
                emptyList()
            }

            LaunchedEffect(Unit) {
                umkmViewModel.loadAllUmkm()
            }

            AddPromoScreen(
                umkmList = umkmList,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("location") {
            UmkmLocationScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController, authViewModel)
        }
    }
}

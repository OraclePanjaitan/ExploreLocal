package com.example.explorelocal.ui.screen.promo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.explorelocal.navigation.BottomNavItem
import com.example.explorelocal.ui.components.PromoCard
import com.example.explorelocal.ui.theme.PrimaryPurple
import com.example.explorelocal.viewmodel.PromoState
import com.example.explorelocal.viewmodel.PromoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoListScreen(
    navController: NavController,
    promoViewModel: PromoViewModel = viewModel()
) {
    val state by promoViewModel.promoState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val items = listOf(
        BottomNavItem.Umkm,
        BottomNavItem.Promo,
        BottomNavItem.Location,
        BottomNavItem.Profile
    )

    LaunchedEffect(Unit) {
        promoViewModel.loadAllPromo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Promo & Diskon") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = PrimaryPurple
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryPurple,
                            selectedTextColor = PrimaryPurple,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = PrimaryPurple.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_promo") },
                containerColor = PrimaryPurple
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Promo",
                    tint = Color.White
                )
            }
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {


            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari Promo UMKM") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                shape = RoundedCornerShape(50)
            )

            Spacer(Modifier.height(16.dp))

            when (state) {
                is PromoState.Loading -> {
                    CircularProgressIndicator()
                }

                is PromoState.PromoList -> {
                    val promos = (state as PromoState.PromoList).data
                        .filter {
                            it.judul.contains(searchQuery, ignoreCase = true)
                        }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(promos) { promo ->
                            PromoCard(promo)
                        }
                    }
                }

                is PromoState.Error -> {
                    Text(
                        text = (state as PromoState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }
        }
    }
}

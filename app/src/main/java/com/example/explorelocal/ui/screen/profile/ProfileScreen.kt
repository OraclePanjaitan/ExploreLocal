package com.example.explorelocal.ui.screen.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.navigation.BottomNavItem
import com.example.explorelocal.ui.theme.PrimaryPurple
import com.example.explorelocal.viewmodel.AuthViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val items = listOf(
        BottomNavItem.Umkm,
        BottomNavItem.Promo,
        BottomNavItem.Location,
        BottomNavItem.Profile
    )

    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn(context)
    }

    LaunchedEffect(userState) {
        when(userState) {
            is UserState.LoggedOut -> {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    // Dialog konfirmasi logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(context)
                    }
                ) {
                    Text("Ya", color = PrimaryPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
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
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = userState) {
                is UserState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryPurple)
                    }
                }
                is UserState.LoggedIn -> {
                    val user = state.user

                    Spacer(modifier = Modifier.height(32.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = PrimaryPurple.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(60.dp),
                            tint = PrimaryPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Email
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = PrimaryPurple
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Email",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = user?.email ?: "Email tidak tersedia",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Logout Button
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                is UserState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.isUserLoggedIn(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryPurple
                            )
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                else -> {}
            }
        }
    }
}


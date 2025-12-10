package com.example.explorelocal.ui.screen.umkm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.explorelocal.LoadingComponent
import com.example.explorelocal.data.model.Umkm
import com.example.explorelocal.navigation.AppNavigation
import com.example.explorelocal.navigation.BottomNavItem
import com.example.explorelocal.ui.theme.PrimaryPurple
import com.example.explorelocal.viewmodel.UmkmState
import com.example.explorelocal.viewmodel.UmkmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmkmListScreen(
    navController: NavController,
    viewModel: UmkmViewModel = viewModel()
) {
    val umkmState by viewModel.umkmState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Form states
    var nama by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }

    val items = listOf(
        BottomNavItem.Umkm,
        BottomNavItem.Promo,
        BottomNavItem.Location,
        BottomNavItem.Profile
    )

    LaunchedEffect(Unit) {
        viewModel.loadAllUmkm()
    }

    // Dialog untuk tambah UMKM
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah UMKM Baru") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it },
                        label = { Text("Nama UMKM *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = kategori,
                        onValueChange = { kategori = it },
                        label = { Text("Kategori") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deskripsi,
                        onValueChange = { deskripsi = it },
                        label = { Text("Deskripsi") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fotoUrl,
                        onValueChange = { fotoUrl = it },
                        label = { Text("URL Foto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nama.isNotBlank()) {
                            viewModel.insertUmkm(
                                nama = nama,
                                kategori = kategori.ifBlank { null },
                                deskripsi = deskripsi.ifBlank { null },
                                fotoUrl = fotoUrl.ifBlank { null }
                            )
                            showAddDialog = false
                            // Reset form
                            nama = ""
                            kategori = ""
                            deskripsi = ""
                            fotoUrl = ""
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar UMKM") },
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
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryPurple
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah UMKM",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (val state = umkmState) {
                is UmkmState.Loading -> LoadingComponent()

                is UmkmState.UmkmList -> {
                    LazyColumn {
                        items(state.data) { umkm ->
                            UmkmItem(umkm = umkm)
                        }
                    }
                }

                is UmkmState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is UmkmState.Success -> {
                    LaunchedEffect(state) {
                        // Show snackbar atau toast
                        viewModel.resetState()
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun UmkmItem(umkm: Umkm) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = umkm.nama,

                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            umkm.kategori?.let {
                Text(text = it, color = Color.Gray, fontSize = 14.sp)
            }
            umkm.deskripsi?.let {
                Text(text = it, fontSize = 14.sp)
            }
        }
    }
}


@Preview
@Composable
fun Previewss(){
    AppNavigation()
}


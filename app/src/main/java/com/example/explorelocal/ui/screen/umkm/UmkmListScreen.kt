package com.example.explorelocal.ui.screen.umkm

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
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
    val context = LocalContext.current
    val umkmState by viewModel.umkmState.collectAsState()
    val uploadedImageUrl by viewModel.uploadedImageUrl.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var nama by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val items = listOf(
        BottomNavItem.Umkm,
        BottomNavItem.Promo,
        BottomNavItem.Location,
        BottomNavItem.Profile
    )

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            viewModel.uploadImage(it, context)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllUmkm()
    }

    // Handle success state
    LaunchedEffect(umkmState) {
        when (umkmState) {
            is UmkmState.Success -> {
                showAddDialog = false
                nama = ""
                kategori = ""
                deskripsi = ""
                selectedImageUri = null
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Dialog untuk tambah UMKM
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                selectedImageUri = null
                viewModel.resetUploadedImage()
            },
            title = { Text("Tambah UMKM Baru") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
                    // Image picker card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                umkmState is UmkmState.Uploading -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = PrimaryPurple)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Uploading...", color = Color.Gray)
                                    }
                                }
                                uploadedImageUrl != null -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(uploadedImageUrl),
                                        contentDescription = "Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                selectedImageUri != null -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = "Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = "Upload Photo",
                                            modifier = Modifier.size(48.dp),
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Tap untuk pilih foto", color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nama.isNotBlank()) {
                            viewModel.insertUmkm(
                                nama = nama,
                                kategori = kategori.ifBlank { null },
                                deskripsi = deskripsi.ifBlank { null },
                                fotoUrl = uploadedImageUrl
                            )
                        }
                    },
                    enabled = umkmState !is UmkmState.Loading && umkmState !is UmkmState.Uploading
                ) {
                    if (umkmState is UmkmState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    selectedImageUri = null
                    viewModel.resetUploadedImage()
                }) {
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadAllUmkm() }) {
                            Text("Coba Lagi")
                        }
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
        Row(modifier = Modifier.padding(16.dp)) {
            // Foto UMKM
            umkm.fotoUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = "Foto ${umkm.nama}",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column {
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
}


@Preview
@Composable
fun Previewss(){
    AppNavigation()
}


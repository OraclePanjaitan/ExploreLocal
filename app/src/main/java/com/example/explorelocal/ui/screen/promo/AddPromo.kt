package com.example.explorelocal.ui.screen.promo

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.explorelocal.data.model.Umkm
import com.example.explorelocal.viewmodel.PromoState
import com.example.explorelocal.viewmodel.PromoViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPromoScreen(
    promoViewModel: PromoViewModel = viewModel(),
    umkmList: List<Umkm>,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by promoViewModel.promoState.collectAsState()
    val uploadedBanner by promoViewModel.uploadedBannerUrl.collectAsState()

    var selectedUmkm by remember { mutableStateOf<Umkm?>(null) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var namaPromo by remember { mutableStateOf("") }
    var deskripsiPromo by remember { mutableStateOf("") }
    var tanggalMulai by remember { mutableStateOf(LocalDate.now().toString()) }
    var tanggalSelesai by remember { mutableStateOf(LocalDate.now().toString()) }

    val jenisPromoOptions = listOf(
        "Cashback",
        "Minimum pembelian",
        "Kupon",
        "Diskon",
        "Bundle"
    )
    var selectedJenisPromo by remember { mutableStateOf(jenisPromoOptions.first()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { promoViewModel.uploadBanner(it, context) }
    }

    LaunchedEffect(state) {
        if (state is PromoState.Success) {
            onSuccess()
            promoViewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Promo",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            Text(
                "Buat promo terbaikmu dan tarik perhatian para penjelajah kuliner!"
            )

            Spacer(Modifier.height(24.dp))

            // ===== PILIH UMKM =====
            Text("Pilih UMKM Anda", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))

            Box {
                OutlinedTextField(
                    value = selectedUmkm?.nama ?: "Daftar UMKM",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedDropdown = true },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                )

                DropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    umkmList.forEach { umkm ->
                        DropdownMenuItem(
                            text = { Text(umkm.nama) },
                            onClick = {
                                selectedUmkm = umkm
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ===== NAMA PROMO =====
            Text("Nama Promo")
            OutlinedTextField(
                value = namaPromo,
                onValueChange = { namaPromo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nama Promo") }
            )

            Spacer(Modifier.height(20.dp))

            // ===== DESKRIPSI =====
            Text("Deskripsi Promo")
            OutlinedTextField(
                value = deskripsiPromo,
                onValueChange = { deskripsiPromo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Deskripsi Promo") },
                maxLines = 5
            )

            Spacer(Modifier.height(20.dp))

            // ===== TANGGAL =====
            Text("Periode Promo")

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = tanggalMulai,
                    onValueChange = { tanggalMulai = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tanggal Mulai") }
                )

                OutlinedTextField(
                    value = tanggalSelesai,
                    onValueChange = { tanggalSelesai = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tanggal Selesai") }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ===== JENIS PROMO =====
            Text("Jenis Promo", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))

            jenisPromoOptions.forEach { jenis ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedJenisPromo = jenis },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedJenisPromo == jenis,
                        onClick = { selectedJenisPromo = jenis }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(jenis)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ===== UPLOAD FOTO =====
            Text("Foto Promo")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Color(0xFFF3E8FF),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(Modifier.height(6.dp))
                    Text("Tap untuk upload gambar")
                }
            }

            Spacer(Modifier.height(30.dp))

            // ===== SUBMIT =====
            Button(
                onClick = {
                    if (selectedUmkm == null || namaPromo.isBlank()) return@Button

                    promoViewModel.insertPromo(
                        umkmId = selectedUmkm!!.id!!,
                        judul = namaPromo,
                        deskripsi = deskripsiPromo,
                        tanggalMulai = tanggalMulai,
                        tanggalSelesai = tanggalSelesai,
                        bannerUrl = uploadedBanner,
                        jenisPromo = selectedJenisPromo
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("KIRIM", fontWeight = FontWeight.Bold)
            }

            if (state is PromoState.Loading || state is PromoState.Uploading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
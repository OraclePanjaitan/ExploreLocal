package com.example.explorelocal.ui.screen.promo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.explorelocal.data.model.Promo
import com.example.explorelocal.viewmodel.PromoViewModel
import com.example.explorelocal.viewmodel.PromoState
import com.example.explorelocal.viewmodel.BannerUploadState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.DateRange
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPromoScreen(
    promoId: String,
    navController: NavController,
    promoViewModel: PromoViewModel = viewModel()
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val umkmNama by promoViewModel.umkmNama.collectAsState()

    val state by promoViewModel.promoState.collectAsState()
    val bannerState by promoViewModel.bannerState.collectAsState()
    val context = LocalContext.current

    var promo by remember { mutableStateOf<Promo?>(null) }
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var tanggalMulai by remember { mutableStateOf("") }
    var tanggalSelesai by remember { mutableStateOf("") }
    var jenisPromo by remember { mutableStateOf("") }
    var bannerUrl by remember { mutableStateOf<String?>(null) }
    var tanggalMulaiApi by remember { mutableStateOf("") }
    var tanggalSelesaiApi by remember { mutableStateOf("") }

    var nama by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    val promoTypes = listOf(
        "Cashback",
        "Minimum Pembelian",
        "Kupon",
        "Diskon",
        "Bundle"
    )
    var expandedJenis by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { promoViewModel.uploadBanner(it, context) }
    }

    LaunchedEffect(promoId) {
        promoViewModel.loadAllPromo()
    }

    LaunchedEffect(state) {
        if (state is PromoState.PromoList) {
            promo = (state as PromoState.PromoList)
                .data
                .find { it.id == promoId }

            promo?.let {
                judul = it.judul
                deskripsi = it.deskripsi ?: ""
                tanggalMulaiApi = it.tanggalMulai ?: ""
                tanggalSelesaiApi = it.tanggalSelesai ?: ""
                tanggalMulai = it.tanggalMulai?.let(::formatFromApi) ?: ""
                tanggalSelesai = it.tanggalSelesai?.let(::formatFromApi) ?: ""
                jenisPromo = it.jenisPromo
                bannerUrl = it.bannerUrl

                promoViewModel.loadUmkmName(it.umkmId)
            }
        }
    }

    LaunchedEffect(bannerState) {
        if (bannerState is BannerUploadState.Success) {
            bannerUrl = (bannerState as BannerUploadState.Success).url
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Promo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        promo?.let {

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                /* ---------- BANNER ---------- */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { imagePicker.launch("image/*") }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(bannerUrl)
                            .crossfade(true)
                            .setParameter("time", System.currentTimeMillis())
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = umkmNama ?: "Memuat UMKM...",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("UMKM") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Nama Promo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Promo") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dateRangePickerState.setSelection(null, null)
                            showDatePicker = true
                        }
                ) {
                    OutlinedTextField(
                        value = if (tanggalMulai.isNotEmpty() && tanggalSelesai.isNotEmpty())
                            "$tanggalMulai - $tanggalSelesai" else "",
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Tanggal Promo") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedJenis,
                    onExpandedChange = { expandedJenis = !expandedJenis }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = jenisPromo,
                        onValueChange = {},
                        label = { Text("Jenis Promo") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJenis)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expandedJenis,
                        onDismissRequest = { expandedJenis = false }
                    ) {
                        promoTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    jenisPromo = type
                                    expandedJenis = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        showConfirmDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Update", fontWeight = FontWeight.Bold)
                }
            }
        }
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Konfirmasi Update") },
                text = { Text("Apakah Anda yakin ingin mengupdate promo ini?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false

                            promoViewModel.updatePromo(
                                id = promoId,
                                promo = promo!!.copy(
                                    judul = judul,
                                    deskripsi = deskripsi,
                                    tanggalMulai = tanggalMulaiApi,
                                    tanggalSelesai = tanggalSelesaiApi,
                                    jenisPromo = jenisPromo,
                                    bannerUrl = bannerUrl
                                )
                            )

                            showSuccessDialog = true
                        }
                    ) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Berhasil") },
                text = { Text("Promo berhasil diperbarui.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        /* ---------- DATE PICKER DIALOG (POSISI BENAR) ---------- */
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis

                        if (start != null && end != null) {
                            tanggalMulai = formatDate(start)
                            tanggalSelesai = formatDate(end)

                            tanggalMulaiApi = formatDateForApi(start)
                            tanggalSelesaiApi = formatDateForApi(end)
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Batal")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    showModeToggle = false
                )
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return formatter.format(Date(millis))
}

private fun formatFromApi(date: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        formatter.format(parser.parse(date)!!)
    } catch (e: Exception) {
        date
    }
}
private fun formatDateForApi(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

package com.example.explorelocal.ui.screen.promo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.explorelocal.viewmodel.BannerUploadState

/* -------------------- DATE HELPERS -------------------- */

private val DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun LocalDate.toMillis(): Long =
    this.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

private fun millisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

/* -------------------- SCREEN -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPromoScreen(
    promoViewModel: PromoViewModel = viewModel(),
    umkmList: List<Umkm>,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val state by promoViewModel.promoState.collectAsState()
    val bannerState by promoViewModel.bannerState.collectAsState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var selectedUmkm by remember { mutableStateOf<Umkm?>(null) }

    var namaPromo by remember { mutableStateOf("") }
    var deskripsiPromo by remember { mutableStateOf("") }

    var tanggalMulai by remember { mutableStateOf(LocalDate.now()) }
    var tanggalSelesai by remember { mutableStateOf(LocalDate.now()) }

    val jenisPromoOptions = listOf(
        "Cashback",
        "Minimum pembelian",
        "Kupon",
        "Diskon",
        "Bundle"
    )
    var selectedJenisPromo by remember { mutableStateOf("Cashback") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            promoViewModel.uploadBanner(it, context)
        }
    }

    val bannerUrl = when (bannerState) {
        is BannerUploadState.Success ->
            (bannerState as BannerUploadState.Success).url
        else -> null
    }

    LaunchedEffect(state) {
        when (val s = state) {
            is PromoState.Success -> {
                successMessage = s.message
                showSuccessDialog = true
            }
            else -> Unit
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Berhasil 🎉") },
            text = { Text(successMessage) },
            confirmButton = {
                TextButton(onClick = {

                    // 🔄 RESET FORM
                    selectedUmkm = null
                    namaPromo = ""
                    deskripsiPromo = ""
                    selectedImageUri = null
                    selectedJenisPromo = "Cashback"
                    tanggalMulai = LocalDate.now()
                    tanggalSelesai = LocalDate.now()

                    // 🔄 RESET STATE VIEWMODEL
                    promoViewModel.resetPromoState()
                    promoViewModel.resetBannerState()

                    showSuccessDialog = false
                }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Promo & Diskon") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {

            Spacer(Modifier.height(12.dp))
            Text(
                "Buat promo terbaikmu dan tarik perhatian para penjelajah kuliner!",
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            /* ---------- UMKM ---------- */
            Text("Pilih UMKM Anda", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            UmkmDropdown(
                umkmList = umkmList,
                selectedUmkm = selectedUmkm,
                onSelected = { selectedUmkm = it }
            )

            Spacer(Modifier.height(20.dp))

            /* ---------- NAMA ---------- */
            Text("Nama Promo", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = namaPromo,
                onValueChange = { namaPromo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nama Promo") }
            )

            Spacer(Modifier.height(20.dp))

            /* ---------- DESKRIPSI ---------- */
            Text("Deskripsi Promo", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = deskripsiPromo,
                onValueChange = { deskripsiPromo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Deskripsi Promo") },
                minLines = 4
            )

            Spacer(Modifier.height(20.dp))

            /* ---------- DATE PICKERS ---------- */
            Text("Pilih Tanggal Mulai dan Tanggal Selesai", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                DatePickerField(
                    label = "Tanggal Mulai",
                    date = tanggalMulai,
                    minDate = LocalDate.now(),
                    onDateSelected = {
                        tanggalMulai = it
                        if (tanggalSelesai.isBefore(it)) {
                            tanggalSelesai = it
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                DatePickerField(
                    label = "Tanggal Selesai",
                    date = tanggalSelesai,
                    minDate = tanggalMulai,
                    onDateSelected = { tanggalSelesai = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            /* ---------- JENIS PROMO ---------- */
            Text("Jenis Promo (Pilih salah satu*)", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))

            jenisPromoOptions.forEach { jenis ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = if (selectedJenisPromo == jenis) 2.dp else 0.dp,
                    border = BorderStroke(
                        1.dp,
                        if (selectedJenisPromo == jenis)
                            MaterialTheme.colorScheme.primary
                        else Color(0xFFE5E7EB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedJenisPromo = jenis }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
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
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            /* ---------- IMAGE ---------- */
            Text("Foto Promo", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF3E8FF), RoundedCornerShape(16.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {

                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Banner Promo",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(Modifier.height(6.dp))
                        Text("Tap to upload image")
                        Text(
                            "PNG / JPG / JPEG",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (bannerState is BannerUploadState.Uploading) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Mengupload foto...",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(32.dp))

            /* ---------- SUBMIT ---------- */
            Button(
                onClick = {
                    when {
                        selectedUmkm == null -> {
                            dialogMessage = "Silakan pilih UMKM terlebih dahulu."
                            showDialog = true
                        }

                        namaPromo.isBlank() -> {
                            dialogMessage = "Nama promo tidak boleh kosong."
                            showDialog = true
                        }

                        selectedImageUri == null -> {
                            dialogMessage = "Silakan pilih foto promo terlebih dahulu."
                            showDialog = true
                        }

                        bannerState is BannerUploadState.Uploading -> {
                            dialogMessage = "Foto promo masih diunggah, mohon tunggu."
                            showDialog = true
                        }

                        bannerState is BannerUploadState.Error -> {
                            dialogMessage = "Upload foto gagal, silakan pilih ulang."
                            showDialog = true
                        }

                        bannerUrl == null -> {
                            dialogMessage = "Banner belum berhasil diupload."
                            showDialog = true
                        }

                        else -> {
                            promoViewModel.insertPromo(
                                umkmId = selectedUmkm!!.id!!,
                                judul = namaPromo,
                                deskripsi = deskripsiPromo,
                                tanggalMulai = tanggalMulai.toString(),
                                tanggalSelesai = tanggalSelesai.toString(),
                                bannerUrl = bannerUrl,
                                jenisPromo = selectedJenisPromo
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                // 🔑 CONTENT BUTTON HARUS DI SINI
                Text("KIRIM", fontWeight = FontWeight.Bold)
            }

            if (state is PromoState.Loading || bannerState is BannerUploadState.Uploading) {
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/* -------------------- COMPONENTS -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UmkmDropdown(
    umkmList: List<Umkm>,
    selectedUmkm: Umkm?,
    onSelected: (Umkm) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedUmkm?.nama ?: "Daftar UMKM",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            umkmList.forEach { umkm ->
                DropdownMenuItem(
                    text = { Text(umkm.nama) },
                    onClick = {
                        onSelected(umkm)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* -------------------- DATE FIELD (FIXED) -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    date: LocalDate,
    minDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.toMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return !millisToLocalDate(utcTimeMillis).isBefore(minDate)
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        onDateSelected(millisToLocalDate(it))
                    }
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    Box(
        modifier = modifier.clickable { showDialog = true }
    ) {
        OutlinedTextField(
            value = date.format(DATE_FORMATTER),
            onValueChange = {},
            enabled = false, // 🔑 CRITICAL LINE
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.CalendarMonth, null)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

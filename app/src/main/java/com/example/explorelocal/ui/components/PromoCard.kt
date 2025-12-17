package com.example.explorelocal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.explorelocal.data.model.Promo
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import com.example.explorelocal.ui.theme.PrimaryPurple
import androidx.compose.foundation.BorderStroke
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@Composable
fun PromoCard(
    promo: Promo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {

            /* ---------- BANNER ---------- */
            AsyncImage(
                model = promo.bannerUrl,
                contentDescription = promo.judul,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            /* ---------- CONTENT ---------- */
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = promo.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = promo.deskripsi ?: "",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                /* ---------- BOTTOM ROW (SESUI DESAIN) ---------- */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    /* ---- TANGGAL ---- */
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = "Berlaku:\n${
                                formatTanggalIndo(promo.tanggalMulai)
                            } – ${
                                formatTanggalIndo(promo.tanggalSelesai)
                            }",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryPurple
                        )
                    }

                    /* ---- CHIP JENIS PROMO ---- */
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                promo.jenisPromo,
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryPurple
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = PrimaryPurple.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = PrimaryPurple.copy(alpha = 0.4f)
                        )
                    )

                    Spacer(Modifier.width(6.dp))

                    /* ---- DELETE ICON ---- */
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Promo",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

private fun formatTanggalIndo(date: String?): String {
    return try {
        if (date.isNullOrEmpty()) return "-"
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        formatter.format(parser.parse(date)!!)
    } catch (e: Exception) {
        date ?: "-"
    }
}

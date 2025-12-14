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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.explorelocal.ui.components.PromoCard
import com.example.explorelocal.viewmodel.PromoState
import com.example.explorelocal.viewmodel.PromoViewModel

@Composable
fun PromoListScreen(
    promoViewModel: PromoViewModel = viewModel(),
    onAddPromo: () -> Unit
) {
    val state by promoViewModel.promoState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        promoViewModel.loadAllPromo()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPromo,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Promo")
            }
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                "Promo & Diskon",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

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

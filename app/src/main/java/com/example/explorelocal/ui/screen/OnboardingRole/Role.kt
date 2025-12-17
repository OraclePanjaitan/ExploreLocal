package com.example.explorelocal.ui.screen.OnboardingRole

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.explorelocal.R

@Composable
fun OnboardingRole(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2E4FF)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Image(
            painter = painterResource(id = R.drawable.role),
            contentDescription = "PilihRole",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(260.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Pilih peran Anda",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Pilih UMKM jika ingin mempromosikan usaha Anda",
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE0E0E0)
            )
            Text(
                text = "Atau",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE0E0E0)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Pilih PENJELAJAH jika ingin menjelajahi kuliner lokal",
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val containerHeight = 200.dp

            // ✅ UMKM (Owner role)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.height(containerHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.umkm),
                        contentDescription = "UMKM",
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .clickable {
                                // Navigate dengan parameter role=owner
                                navController.navigate("login?role=owner")
                            }
                    )
                }
            }

            // ✅ Penjelajah (User role)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.height(containerHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.penjelajah),
                        contentDescription = "Penjelajah",
                        modifier = Modifier
                            .width(180.dp)
                            .height(200.dp)
                            .clickable {
                                // Navigate dengan parameter role=user
                                navController.navigate("login?role=user")
                            }
                    )
                }
            }
        }
    }
}

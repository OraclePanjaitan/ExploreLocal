package com.example.explorelocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.navigation.AppNavigation
import com.example.explorelocal.ui.theme.ExploreLocalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExploreLocalTheme {
                AppNavigation()
            }
        }
    }



//    @Composable
//    fun MainScreen(
//        viewModel: SupabaseAuthViewModel = viewModel(),
//    ){
//        val context = LocalContext.current
//        val userState by viewModel.userState
//
//        var userEmail by remember { mutableStateOf("") }
//        var userPassword by remember { mutableStateOf("") }
//
//        var currentUserState by remember{mutableStateOf("")}
//
//        LaunchedEffect(Unit) {
//            viewModel.isUserLoggedIn(
//                context,
//            )
//        }
//
//        Column (
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp)
//        ){
//            TextField(
//                value = userEmail,
//                placeholder = {
//                    Text(text = "Enter email")
//                },
//                onValueChange = {
//                    userEmail = it
//                })
//            Spacer(Modifier.padding(8.dp))
//            TextField(
//                value = userPassword,
//                placeholder = {
//                    Text(text = "Enter password")
//                },
//                onValueChange = {
//                    userPassword = it
//                })
//            Spacer(Modifier.padding(8.dp))
//            Button({
//                viewModel.signUp(
//                    context,
//                    userEmail,
//                    userPassword,
//                )
//            }) {
//                Text("Sign up")
//            }
//            Button({
//                viewModel.login(
//                    context,
//                    userEmail,
//                    userPassword,
//                )
//            }) {
//                Text("Login")
//            }
//            Button(
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                onClick = {
//                    viewModel.logout(context)
//                }) {
//                Text("Logout")
//            }
//
//            when(userState){
//                is UserState.Loading ->{
//                    LoadingComponent()
//                }
//                is UserState.Success ->{
//                    val message = (userState as UserState.Success).message
//                    currentUserState = message
//                }
//                is UserState.Error ->{
//                    val message = (userState as UserState.Error).message
//                    currentUserState = message
//                }
//            }
//
//            if(currentUserState.isNotEmpty()){
//                Text(text = currentUserState)
//            }
//        }
//    }
}



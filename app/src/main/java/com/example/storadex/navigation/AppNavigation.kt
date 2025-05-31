package com.example.storadex.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.storadex.viewmodel.AlbumViewModel
import com.example.storadex.viewmodel.LoginScreenViewModel
import com.example.storadex.viewmodel.UserViewModel
import com.example.storadex.views.AlbumScreen
import com.example.storadex.views.CardsScreen
import com.example.storadex.views.HomeScreen
import com.example.storadex.views.LoginScreen
import com.example.storadex.views.OpeningScreen
import com.example.storadex.views.UserScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    loginViewModelParameter: LoginScreenViewModel,
    startDestination: String
) {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppScreens.Login.route) {
            showBottomBar = false
            LoginScreen(navController, loginViewModelParameter)
        }
        composable(AppScreens.Home.route) {
            showBottomBar = true
            Scaffold(
                bottomBar = { if (showBottomBar) BottomBar(navController) }
            ) { innerPadding: PaddingValues ->
                HomeScreen(
                    navController = navController,

                )
            }
        }
        composable(AppScreens.Album.route) {
            showBottomBar = true
            Scaffold(
                bottomBar = { if (showBottomBar) BottomBar(navController) }
            ) { innerPadding: PaddingValues ->
                val albumViewModelInstance: AlbumViewModel = viewModel()
                AlbumScreen(
                    navController = navController,
                    viewModel = albumViewModelInstance,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
        composable(
            route = "${AppScreens.Cards.route}/{collectionId}/{collectionName}",
            arguments = listOf(
                navArgument("collectionId") { type = NavType.StringType },
                navArgument("collectionName") { type = NavType.StringType }
            )
        ) { backStack ->
            showBottomBar = true
            Scaffold(
                bottomBar = { if (showBottomBar) BottomBar(navController) }
            ) { innerPadding: PaddingValues ->
                val colId = backStack.arguments?.getString("collectionId")!!
                val colName = backStack.arguments?.getString("collectionName")!!
                CardsScreen(
                    navController = navController,
                    collectionId = colId,
                    collectionName = colName,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
        composable("social") {
            showBottomBar = true
            Scaffold(
                bottomBar = { if (showBottomBar) BottomBar(navController) }
            ) { innerPadding: PaddingValues ->
                // TODO: implementar SocialScreen
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                )
            }
        }
        composable("profile") {
            val userViewModelInstance: UserViewModel = viewModel()
            UserScreen(navController = navController, userViewModel = userViewModelInstance)
        }
        composable("simulator") {
            showBottomBar = false
            Scaffold(
                bottomBar = { if (showBottomBar) BottomBar(navController) }
            ) { innerPadding: PaddingValues ->
                OpeningScreen(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }

    }
}
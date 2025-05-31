package com.example.storadex.views

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.storadex.R
import com.example.storadex.model.CardData
import com.example.storadex.navigation.BottomBar
import com.example.storadex.viewmodel.OpeningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpeningScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: OpeningViewModel = viewModel(
        factory = OpeningViewModel.provideFactory(
            context.applicationContext as Application
        )
    )

    val collections by viewModel.collections.collectAsState()
    val selectedCollection by viewModel.selectedCollection.collectAsState()
    val randomCards by viewModel.randomCards.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = selectedCollection?.name ?: "Selecciona colección",
                        color = colorResource(id = R.color.pikayelow)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.detailblue)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = colorResource(id = R.color.pikayelow)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Colecciones",
                                tint = colorResource(id = R.color.pikayelow)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            collections.forEach { collection ->
                                DropdownMenuItem(
                                    text = { Text(collection.name) },
                                    onClick = {
                                        viewModel.selectCollection(collection)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        // Cambiamos a Box para tener más control sobre la disposición
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 160.dp)  // Ajusta según sea necesario
                .background(colorResource(id = R.color.baseblue))
        ) {
            // Contenido principal (cartas o mensaje)
            if (randomCards.isNotEmpty()) {
                CardResults(
                    randomCards,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            } else {
                Text(
                    text = "Pulsa OPEN para abrir un sobre",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            }

            // Botón OPEN - ahora fijo en la parte inferior
            Button(
                onClick = { viewModel.openPack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.detailblue),
                    contentColor = colorResource(id = R.color.pikayelow)
                )
            ) {
                Text("OPEN", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun CardResults(
    cards: List<CardData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Has obtenido estas cartas!",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cards) { card ->
                Card(
                    modifier = Modifier
                        .size(120.dp)
                ) {
                    if (card.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = card.imageUrl,
                            contentDescription = card.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = card.name,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
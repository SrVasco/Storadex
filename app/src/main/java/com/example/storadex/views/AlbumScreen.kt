package com.example.storadex.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.storadex.model.CollectionData
import com.example.storadex.viewmodel.AlbumViewModel

@Composable
fun AlbumScreen(
    navController: NavController,
    viewModel: AlbumViewModel,
    modifier: Modifier = Modifier // <--- AÑADIDO: Acepta el parámetro modifier
) {
    val collections by viewModel.collections.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCollections()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (collections.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando colecciones...")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(collections) { collection ->
                    CollectionCard(collection) {
                        // Navegar a CardsScreen con ID y nombre
                        navController.navigate("cards/${collection.id}/${collection.name}")
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionCard(
    collection: CollectionData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = collection.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "${collection.cards.size} cartas", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
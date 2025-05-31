// OpeningViewModel.kt
package com.example.storadex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storadex.model.CardData
import com.example.storadex.model.CollectionData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class OpeningViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()

    // Estado para las colecciones disponibles
    private val _collections = MutableStateFlow<List<CollectionData>>(emptyList())
    val collections: StateFlow<List<CollectionData>> = _collections

    // Colección seleccionada
    private val _selectedCollection = MutableStateFlow<CollectionData?>(null)
    val selectedCollection: StateFlow<CollectionData?> = _selectedCollection

    // Cartas aleatorias resultantes
    private val _randomCards = MutableStateFlow<List<CardData>>(emptyList())
    val randomCards: StateFlow<List<CardData>> = _randomCards

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            try {
                val result = db.collection("collections").get().await()
                val collectionsList = result.documents.map { doc ->
                    CollectionData(
                        id = doc.id,
                        name = doc.getString("name") ?: ""
                    )
                }
                _collections.value = collectionsList
                // Seleccionar la primera colección por defecto
                if (collectionsList.isNotEmpty()) {
                    selectCollection(collectionsList[0])
                }
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun selectCollection(collection: CollectionData) {
        _selectedCollection.value = collection
    }

    fun openPack() {
        viewModelScope.launch {
            val collectionId = _selectedCollection.value?.id ?: return@launch
            try {
                val cardsSnapshot = db.collection("collections")
                    .document(collectionId)
                    .collection("cards")
                    .get()
                    .await()

                val allCards = cardsSnapshot.documents.map { doc ->
                    CardData(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        idcol = doc.getLong("idcol")?.toInt() ?: 0
                    )
                }

                // Seleccionar 5 cartas aleatorias
                if (allCards.size >= 5) {
                    val shuffled = allCards.shuffled()
                    _randomCards.value = shuffled.take(5)
                } else {
                    // Si hay menos de 5 cartas, mostrar todas
                    _randomCards.value = allCards
                }

            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OpeningViewModel(application) as T
            }
        }
    }
}
package com.example.storadex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storadex.model.CardData
import com.example.storadex.model.CollectionData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AlbumViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _collections = MutableStateFlow<List<CollectionData>>(emptyList())
    val collections: StateFlow<List<CollectionData>> get() = _collections

    fun getCollections() = viewModelScope.launch {
        try {
            // 1) Bajar todas las colecciones
            val colSnap = db.collection("collections").get().await()

            // 2) Por cada colección, bajar sus cartas
            val list = colSnap.documents.map { doc ->
                val cardsSnap = doc.reference
                    .collection("cards")
                    .get()
                    .await()

                val cards = cardsSnap.documents.map { c ->
                    CardData(
                        id = c.id,
                        name = c.getString("name") ?: "",
                        imageUrl = c.getString("image") ?: ""
                    )
                }

                CollectionData(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    cards = cards
                )
            }

            _collections.value = list

        } catch (e: Exception) {
            Log.e("AlbumVM", "Error cargando colecciones y cartas", e)
        }
    }
}
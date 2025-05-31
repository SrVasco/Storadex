package com.example.storadex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.storadex.model.CardData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardsViewModel(
    application: Application,
    private val collectionId: String
) : AndroidViewModel(application) {

    private val _cards = MutableStateFlow<List<CardData>>(emptyList())
    val cards: StateFlow<List<CardData>> get() = _cards

    init {
        getCards()
    }

    private fun getCards() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val db = FirebaseFirestore.getInstance()

            db.collection("collections")
                .document(collectionId)
                .collection("cards")
                .orderBy("idcol")
                .get()
                .addOnSuccessListener { snap ->
                    val context = getApplication<Application>().applicationContext
                    val imageLoader = ImageLoader(context)

                    val list = snap.documents.map { c ->
                        val imageUrl = c.getString("imageUrl") ?: ""
                        if (imageUrl.isNotEmpty()) {
                            val request = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .build()
                            imageLoader.enqueue(request)
                        }
                        CardData(
                            id = c.id,
                            name = c.getString("name") ?: "",
                            imageUrl = imageUrl
                        )
                    }

                    // Ahora, obtenemos el estado de cada carta para este usuario
                    db.collection("users")
                        .document(userId)
                        .collection("cards")
                        .get()
                        .addOnSuccessListener { userSnap ->
                            val collectedIds = userSnap.documents
                                .filter { it.getBoolean("isCollected") == true }
                                .map { it.id }

                            // Ahora marca las cartas como "collected"
                            val updatedList = list.map { card ->
                                if (collectedIds.contains(card.id)) {
                                    card.copy(isCollected = true)
                                } else {
                                    card.copy(isCollected = false)
                                }
                            }

                            _cards.value = updatedList
                        }
                }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            collectionId: String
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.AndroidViewModelFactory(application) {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CardsViewModel(application, collectionId) as T
                }
            }
    }

    fun updateCardStatus(cardId: String, isCollected: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userCardsRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("cards")
            .document(cardId)

        val data = mapOf("isCollected" to isCollected)

        userCardsRef.set(data)
    }
}

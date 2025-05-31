package com.example.storadex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storadex.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadUserData()
    }

    fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("UserViewModel", "Cargando datos para UID: ${currentUser.uid}")

            db.collection("users")
                .whereEqualTo("user_id", currentUser.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        Log.d("UserViewModel", "Documento encontrado: ${document.data}")

                        val userId = document.getString("user_id") ?: ""
                        val displayName = document.getString("display_name") ?: ""
                        val avatarUrl = document.getString("avatar_url") ?: ""
                        val quote = document.getString("quote") ?: ""
                        val profession = document.getString("profession") ?: ""
                        val id = document.id

                        _userData.value = UserData(
                            id = id,
                            userId = userId,
                            displayName = displayName,
                            avatarUrl = avatarUrl,
                            quote = quote,
                            profession = profession
                        )
                    } else {
                        Log.d("UserViewModel", "No existe el usuario con user_id=${currentUser.uid}")
                        _userData.value = null
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("UserViewModel", "Error al obtener usuario", e)
                    _userData.value = null
                }
        } else {
            Log.d("UserViewModel", "No hay usuario logueado actualmente")
            _userData.value = null
        }
    }

}

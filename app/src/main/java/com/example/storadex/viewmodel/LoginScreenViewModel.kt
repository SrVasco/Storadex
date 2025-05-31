package com.example.storadex.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storadex.data.UserRepository
import com.google.firebase.auth.FirebaseAuth

class LoginScreenViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    // Maneja el error (opcional)
                }
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Asegúrate de crear el usuario en Firestore solo si no existe (¡esto evita duplicados!)
                        userRepository.createUserIfNotExists(
                            user,
                            displayName = user.email ?: "Usuario",
                            avatarUrl = "" // puedes poner un avatar por defecto
                        )
                        onSuccess()
                    }
                } else {
                    // Maneja el error (opcional)
                }
            }
    }
}

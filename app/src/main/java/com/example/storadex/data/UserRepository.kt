package com.example.storadex.data

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun createUserIfNotExists(user: FirebaseUser, displayName: String, avatarUrl: String) {
        val userDocRef = db.collection("users").document(user.uid)

        val newUser = mapOf(
            "user_id" to user.uid,
            "display_name" to displayName,
            "avatar_url" to avatarUrl,
            "quote" to "",
            "profession" to ""
        )

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    userDocRef.set(newUser)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Usuario creado correctamente")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error al crear usuario", e)
                        }
                } else {
                    Log.d("Firestore", "El usuario ya existe, no se crea duplicado")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al verificar existencia del usuario", e)
            }
    }
}
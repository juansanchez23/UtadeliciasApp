package com.appmoviles.utadeliciasapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// Método en TokenManager para gestionar el envío del token
object TokenManager {
    fun sendRegistrationToServer(token: String) {
        // Aquí puedes implementar el envío del token al servidor o Firestore.
        android.util.Log.d("TokenManager", "Token enviado al servidor: $token")
        // Por ejemplo, puedes guardar el token en Firestore con el UID del usuario.
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val db = FirebaseFirestore.getInstance()
            db.collection("user-info").document(it.uid)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    android.util.Log.d("TokenManager", "Token actualizado en Firestore")
                }
                .addOnFailureListener { e ->
                    android.util.Log.w("TokenManager", "Error al actualizar el token", e)
                }
        }
    }
}

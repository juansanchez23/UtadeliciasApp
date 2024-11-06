package com.appmoviles.utadeliciasapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object TokenManager {
    fun sendRegistrationToServer(token: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let {
            val userDocRef = db.collection("userTokens").document(currentUser.uid)
            val tokenData = hashMapOf("token" to token)

            userDocRef.set(tokenData)
                .addOnSuccessListener {
                    android.util.Log.d("TokenManager", "Token saved to Firestore successfully!")
                }
                .addOnFailureListener {
                    android.util.Log.w("TokenManager", "Error saving token to Firestore", it)
                }
        }
    }
}

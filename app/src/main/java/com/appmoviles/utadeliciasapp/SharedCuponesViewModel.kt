package com.appmoviles.utadeliciasapp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SharedCuponesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val cuponesCollection = db.collection("Cupones")
    private val auth = FirebaseAuth.getInstance()

    private val _cupones = MutableLiveData<List<Cupones>>()
    val cupones: LiveData<List<Cupones>> = _cupones

    fun getCuponesForCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Ahora obtenemos los cupones específicos del usuario comercio
            db.collection("usuarios_comercio")
                .document(currentUser.uid)
                .collection("cupones")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val cuponesLista = mutableListOf<Cupones>()
                    for (document in querySnapshot) {
                        val id = document.id
                        val nombre = document.getString("Nombre") ?: ""
                        val descripcion = document.getString("Descripcion") ?: ""
                        val imagenUrl = document.getString("imagenUrl") ?: ""
                        cuponesLista.add(Cupones(id, nombre, descripcion, imagenUrl))
                    }
                    _cupones.value = cuponesLista
                }
        }
    }
}

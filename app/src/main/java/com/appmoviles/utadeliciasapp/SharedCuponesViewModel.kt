package com.appmoviles.utadeliciasapp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SharedCuponesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val cuponesCollection = db.collection("Cupones")

    private val _cupones = MutableLiveData<List<Cupones>>()
    val cupones: LiveData<List<Cupones>> = _cupones

    fun getCupones() {
        cuponesCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val cuponesLista = mutableListOf<Cupones>()
                for (document in querySnapshot) {
                    val id = document.id
                    val nombre = document.getString("Nombre") ?: ""
                    val descripcion = document.getString("Descripcion") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""

                    cuponesLista.add(Cupones(id, nombre, descripcion,imagenUrl))

                }
                _cupones.value = cuponesLista
            }
    }

    // Aquí puedes agregar métodos para agregar, actualizar y eliminar cupones
    // que actualizarán Firestore y luego llamarán a getCupones() para refrescar la lista
}
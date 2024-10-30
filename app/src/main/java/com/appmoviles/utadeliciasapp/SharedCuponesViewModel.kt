package com.appmoviles.utadeliciasapp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class SharedCuponesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userCupones = MutableLiveData<List<Cupones>>()
    val userCupones: LiveData<List<Cupones>> = _userCupones

    private val _allCupones = MutableLiveData<List<Cupones>>()
    val allCupones: LiveData<List<Cupones>> = _allCupones

    fun getCuponesForCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
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
                        val userId = document.getString("userId") ?: ""
                        cuponesLista.add(Cupones(id, nombre, descripcion, imagenUrl,userId))
                    }
                    _userCupones.value = cuponesLista
                }
        }
    }

    fun getAllCupones() {
        val cuponesLista = mutableListOf<Cupones>()

        // Paso 1: Obtenemos los IDs de los usuarios desde "user_info"
        db.collection("user-info")
            .get()
            .addOnSuccessListener { userInfoSnapshot ->
                val totalUsers = userInfoSnapshot.size()
                var completedTasks = 0

                if (totalUsers == 0) {
                    _allCupones.value = cuponesLista
                    return@addOnSuccessListener
                }

                // Paso 2: Para cada usuario, buscamos en "usuarios_comercio" y obtenemos los cupones
                for (userDocument in userInfoSnapshot) {
                    val userId = userDocument.id

                    db.collection("usuarios_comercio")
                        .document(userId)
                        .collection("cupones")
                        .get()
                        .addOnSuccessListener { cuponesSnapshot ->
                            for (document in cuponesSnapshot) {
                                val id = document.id
                                val nombre = document.getString("Nombre") ?: ""
                                val descripcion = document.getString("Descripcion") ?: ""
                                val imagenUrl = document.getString("imagenUrl") ?: ""
                                val userId = document.getString("userId") ?: ""
                                cuponesLista.add(Cupones(id, nombre, descripcion, imagenUrl, userId))
                            }

                            completedTasks++
                            if (completedTasks == totalUsers) {
                                _allCupones.value = cuponesLista
                            }
                        }
                        .addOnFailureListener { e ->
                            completedTasks++
                            if (completedTasks == totalUsers) {
                                _allCupones.value = cuponesLista
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                // Manejo de error en caso de falla al obtener "user_info"
            }
    }

}

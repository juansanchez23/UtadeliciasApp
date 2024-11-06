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
    private fun fetchComercioName(userId: String, cupon: Cupones, cuponesLista: MutableList<Cupones>, totalToProcess: Int, processed: MutableList<Int>, callback: () -> Unit) {
        db.collection("user-info")
            .document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                cupon.nombreComercio = userDoc.getString("nombre") ?: "Comercio Desconocido"
                cuponesLista.add(cupon)
                processed[0]++
                if (processed[0] == totalToProcess) {
                    callback()
                }
            }
            .addOnFailureListener {
                cupon.nombreComercio = "Comercio Desconocido"
                cuponesLista.add(cupon)
                processed[0]++
                if (processed[0] == totalToProcess) {
                    callback()
                }
            }
    }
    fun getAllCupones() {
        val cuponesLista = mutableListOf<Cupones>()

        db.collection("user-info")
            .get()
            .addOnSuccessListener { userInfoSnapshot ->
                for (userDocument in userInfoSnapshot.documents) {
                    val userId = userDocument.id
                    db.collection("usuarios_comercio")
                        .document(userId)
                        .collection("cupones")
                        .get()
                        .addOnSuccessListener { cuponesSnapshot ->
                            for (document in cuponesSnapshot) {
                                val cupon = Cupones(
                                    id = document.id,
                                    nombre = document.getString("Nombre") ?: "",
                                    descripcion = document.getString("Descripcion") ?: "",
                                    imagenUrl = document.getString("imagenUrl") ?: "",
                                    userId = document.getString("userId") ?: userId,
                                    nombreComercio = userDocument.getString("nombre") ?: "Comercio Desconocido"
                                )
                                cuponesLista.add(cupon)
                            }
                            _allCupones.value = cuponesLista.sortedBy { it.nombreComercio }
                        }
                }
            }
    }

    fun getCuponesForCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val cuponesLista = mutableListOf<Cupones>()
            val processedCount = mutableListOf(0)

            db.collection("usuarios_comercio")
                .document(currentUser.uid)
                .collection("cupones")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val totalCupones = querySnapshot.size()

                    if (totalCupones == 0) {
                        _userCupones.value = emptyList()
                        return@addOnSuccessListener
                    }

                    for (document in querySnapshot) {
                        val cupon = Cupones(
                            id = document.id,
                            nombre = document.getString("Nombre") ?: "",
                            descripcion = document.getString("Descripcion") ?: "",
                            imagenUrl = document.getString("imagenUrl") ?: "",
                            userId = document.getString("userId") ?: currentUser.uid
                        )

                        fetchComercioName(currentUser.uid, cupon, cuponesLista, totalCupones, processedCount) {
                            _userCupones.value = cuponesLista
                        }
                    }
                }
        }
    }
}

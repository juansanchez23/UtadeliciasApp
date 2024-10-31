package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ajustes_cliente : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajustes_cliente, container, false)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Llama a la función para obtener y mostrar la información del cliente
        obtenerInfoCliente(view)

        return view
    }

    private fun obtenerInfoCliente(view: View) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Acceder al documento en Firestore correspondiente al usuario actual
            db.collection("user-info").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre") ?: "Sin nombre"
                        val apellido = document.getString("apellido") ?: "Sin apellido"
                        val email = document.getString("email") ?: "Sin email"

                        // Mostrar los datos en la interfaz
                        setupCliente(view, email, nombre, apellido)
                    } else {
                        setupCliente(view, "Sin email", "Usuario no encontrado", "")
                    }
                }
                .addOnFailureListener {
                    setupCliente(view, "Error", "Error al obtener datos", "")
                }
        } else {
            setupCliente(view, "Sin usuario", "No hay sesión", "")
        }
    }

    private fun setupCliente(view: View, email: String, name: String, lastname: String) {
        val emailTextView = view.findViewById<TextView>(R.id.emailTextViewcl)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextViewcl)
        val lastnameTextView = view.findViewById<TextView>(R.id.lastnameTextViewcl)
        val logOutButton = view.findViewById<TextView>(R.id.logOutbuttoncl)

        // Mostrar el correo, nombre y apellido
        emailTextView.text = "Email:\n      $email"
        nameTextView.text = "Nombre:\n      $name"
        lastnameTextView.text = "Apellido:\n      $lastname"

        // Cerrar sesión
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ajustes_cliente()
    }
}

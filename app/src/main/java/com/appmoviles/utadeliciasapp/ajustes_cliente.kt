package com.appmoviles.utadeliciasapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
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
                        val foto_perfil = document.getString("profileImageUrl") ?:"Sin foto de perfil"

                        // Mostrar los datos en la interfaz
                        setupCliente(view, email, nombre, apellido, foto_perfil)
                    } else {
                        setupCliente(view, "Sin email", "Usuario no encontrado", "", "Sin foto de perfil")
                    }
                }
                .addOnFailureListener {
                    setupCliente(view, "Error", "Error al obtener datos", "", "Sin foto de perfil")
                }
        } else {
            setupCliente(view, "Sin usuario", "No hay sesión", "","Sin foto de perfil")
        }
    }

    private fun setupCliente(view: View, email: String, name: String, lastname: String , foto_perfil: String) {
        val emailTextView = view.findViewById<TextView>(R.id.emailTextViewcl)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextViewcl)
        val lastnameTextView = view.findViewById<TextView>(R.id.lastnameTextViewcl)
        val logOutButton = view.findViewById<TextView>(R.id.logOutbuttoncl)
        var ivUsuario = view.findViewById<ShapeableImageView>(R.id.ivUsuario)

        // Mostrar el correo, nombre y apellido
        emailTextView.text = "Email:\n      $email"
        nameTextView.text = "Nombre:\n      $name"
        lastnameTextView.text = "Apellido:\n      $lastname"

        if (foto_perfil.isNotEmpty() && foto_perfil != "Sin foto de perfil") {
            Glide.with(this)
                .load(foto_perfil)
                .placeholder(R.drawable.cabitonormal) // Imagen por defecto mientras se carga
                .into(ivUsuario)
        } else {
            ivUsuario.setImageResource(R.drawable.cabitonormal)
        }

        // Cerrar sesión
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ajustes_cliente()
    }
}

package com.appmoviles.utadeliciasapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ajustes_fragmento : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ajustes, container, false)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Llama a la función para obtener y mostrar la información del usuario
        obtenerInfoUsuario(view)

        return view
    }

    private fun obtenerInfoUsuario(view: View) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Acceder al documento en Firestore que corresponde al usuario actual
            db.collection("user-info").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre") ?: "Sin nombre"
                        val apellido = document.getString("apellido") ?: "Sin apellido"
                        val email = document.getString("email") ?: "Sin email"
                        val foto_perfil = document.getString("profileImageUrl") ?:"Sin foto de perfil"

                        // Llama a setup para mostrar los datos en pantalla
                        setup(view, email, "Firebase", nombre, apellido, foto_perfil)
                    } else {
                        setup(view, "Sin email", "Firebase", "Usuario no encontrado", "", "Sin foto de perfil")
                    }
                }
                .addOnFailureListener {
                    setup(view, "Error", "Firebase", "Error al obtener datos", "", "Sin foto de perfil")
                }
        } else {
            setup(view, "Sin usuario", "Firebase", "No hay sesión", "", "Sin foto de perfil")
        }
    }

    private fun setup(view: View, email: String, provider: String, name: String, lastname: String, foto_perfil: String) {
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)
        val providerTextView = view.findViewById<TextView>(R.id.providerTextView)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val lastnameTextView = view.findViewById<TextView>(R.id.lastnameTextView)
        val logOutButton = view.findViewById<TextView>(R.id.logOutbutton)
        var ivUsuario = view.findViewById<ShapeableImageView>(R.id.ivUsuario)


        // Mostrar el correo, tipo de autenticación, nombre y apellido
        emailTextView.text = "Email:\n      $email"
        providerTextView.text = "Proveedor:\n      $provider"
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
        fun newInstance(email: String, provider: String, name: String, lastname: String, ) =
            ajustes_fragmento().apply {
                arguments = Bundle().apply {
                    putString("email", email)
                    putString("provider", provider)
                    putString("name", name)
                    putString("lastname", lastname)
                }
            }
    }
}

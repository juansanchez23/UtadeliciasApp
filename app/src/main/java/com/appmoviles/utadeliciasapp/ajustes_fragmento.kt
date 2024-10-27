package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ajustes_fragmento : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ajustes, container, false)

        // Setup (Aquí se recibe la información del email y provider desde el Bundle)
        val bundle = arguments
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val name = bundle?.getString("name")
        val lastname =bundle?.getString("lastname")
        setup(view, email ?: "", provider ?: "", name ?: "",lastname ?: "")

        return view
    }

    private fun setup(view: View, email: String, provider: String, name: String,lastname: String) {
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)
        val providerTextView = view.findViewById<TextView>(R.id.providerTextView)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val lastnameTextView = view.findViewById<TextView>(R.id.lastnameTextView)
        val logOutButton = view.findViewById<TextView>(R.id.logOutbutton)

        // Mostrar el correo y el tipo de autenticación
        emailTextView.text = email
        providerTextView.text = provider
        nameTextView.text =name
        lastnameTextView.text= lastname

        // Cerrar sesión
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String, provider: String,name: String, lastname: String) =
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

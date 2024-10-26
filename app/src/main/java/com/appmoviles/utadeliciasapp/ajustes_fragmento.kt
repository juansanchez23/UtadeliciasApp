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
        setup(view, email ?: "", provider ?: "")

        return view
    }

    private fun setup(view: View, email: String, provider: String) {
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)
        val providerTextView = view.findViewById<TextView>(R.id.providerTextView)
        val logOutButton = view.findViewById<TextView>(R.id.logOutbutton)

        // Mostrar el correo y el tipo de autenticación
        emailTextView.text = email
        providerTextView.text = provider

        // Cerrar sesión
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String, provider: String) =
            ajustes_fragmento().apply {
                arguments = Bundle().apply {
                    putString("email", email)
                    putString("provider", provider)
                }
            }
    }
}

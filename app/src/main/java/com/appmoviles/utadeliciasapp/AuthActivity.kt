package com.appmoviles.utadeliciasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType {
    BASIC
}

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase Completa")
        analytics.logEvent("InitScreen", bundle)

        // Setup
        setup()
    }

    private fun setup() {
        title = "Ingresar"
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val logInButton = findViewById<Button>(R.id.logInbutton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        signUpButton.setOnClickListener {
            // Crear un Intent para ir a RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        logInButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Obtener el usuario autenticado
                        val user = it.result?.user
                        if (user != null) {
                            // Obtener la información del usuario de Firestore
                            val db = FirebaseFirestore.getInstance()
                            db.collection("user-info").document(user.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        // Extraer los datos del documento
                                        val nombre = document.getString("nombre") ?: ""
                                        val apellido = document.getString("apellido") ?: ""
                                        val email = document.getString("email") ?: ""
                                        val esComercio = document.getBoolean("esComercio") ?: false

                                        // Dirigir a la actividad correspondiente según esComercio
                                        actividadComercio(email,
                                            ProviderType.BASIC, esComercio, nombre, apellido)
                                    } else {
                                        showAlert() // Si el documento no existe
                                    }
                                }
                                .addOnFailureListener {
                                    showAlert() // Si hay un error al obtener el documento
                                }
                        }
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun actividadComercio(email: String, provider: ProviderType, esComercio: Boolean, nombre: String, apellido: String) {
        if (esComercio) {
            // Si es comercio, redirigir a la actividad correspondiente para dueños de comercio
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
            }
            startActivity(homeIntent)
        } else {
            // Si no es comercio, redirigir a la actividad para clientes
            val clienteIntent = Intent(this, NavCliente::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
                putExtra("nombre", nombre)
                putExtra("apellido", apellido)
            }
            startActivity(clienteIntent)
        }
    }
}

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
import com.google.firebase.messaging.FirebaseMessaging

enum class ProviderType {
    BASIC
}

class AuthActivity : AppCompatActivity() {

    private val TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        setup()
    }

    private fun setup() {
        title = "Ingresar"
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val logInButton = findViewById<Button>(R.id.logInbutton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        logInButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        user?.let {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("user-info").document(user.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val nombre = document.getString("nombre") ?: ""
                                        val apellido = document.getString("apellido") ?: ""
                                        val email = document.getString("email") ?: ""
                                        val esComercio = document.getBoolean("esComercio") ?: false

                                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                            if (!task.isSuccessful) {
                                                android.util.Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                                return@addOnCompleteListener
                                            }
                                            val token = task.result
                                            TokenManager.sendRegistrationToServer(token!!)
                                        }

                                        actividadComercio(email, ProviderType.BASIC, esComercio, nombre, apellido)
                                    } else {
                                        showAlert()
                                    }
                                }
                                .addOnFailureListener {
                                    showAlert()
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
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
            }
            startActivity(homeIntent)
        } else {
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

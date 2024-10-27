package com.appmoviles.utadeliciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase Completa")
        analytics.logEvent("InitScreen", bundle)

        setup()
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setup() {
        title = "Ingresar"
        val signUpButtonRegister = findViewById<Button>(R.id.signUpButtonRegister)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val lastnameEditText = findViewById<EditText>(R.id.lasNameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val switchComercio = findViewById<Switch>(R.id.swich_comercio)

        signUpButtonRegister.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() &&
                nameEditText.text.isNotEmpty() && lastnameEditText.text.isNotEmpty()) {

                // Crear usuario en Firebase Authentication
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    emailEditText.text.toString(), passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        // Obtener el estado del switch
                        val esComercio = switchComercio.isChecked

                        // Crear el mapa de datos para guardar en Firestore
                        val userData = hashMapOf(
                            "nombre" to nameEditText.text.toString(),
                            "apellido" to lastnameEditText.text.toString(),
                            "esComercio" to esComercio, // Guardar el estado del switch
                            "email" to emailEditText.text.toString()
                        )

                        // Guardar los datos en Firestore
                        val db = FirebaseFirestore.getInstance()
                        userId?.let { uid ->
                            db.collection("user-info").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    showHome(emailEditText.text.toString(), ProviderType.BASIC, nameEditText.text.toString(), lastnameEditText.text.toString(), esComercio)
                                }
                                .addOnFailureListener { e ->
                                    showAlert()
                                    Log.w("Firestore", "Error al guardar los datos", e)
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

    private fun showHome(email: String, provider: ProviderType, name: String, lastname: String, esComercio: Boolean) {
        if (esComercio) {
            // Si es comercio, redirigir a la actividad correspondiente para dueños de comercio
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
                putExtra("name", name)
                putExtra("lastname", lastname)
            }
            startActivity(homeIntent)
        } else {
            // Si no es comercio, redirigir a la actividad con barra de navegación
            val homeIntent = Intent(this, NavCliente::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
                putExtra("name", name)
                putExtra("lastname", lastname)
            }
            startActivity(homeIntent)
        }
    }
}

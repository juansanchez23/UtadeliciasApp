package com.appmoviles.utadeliciasapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat



enum class ProviderType {
    BASIC
}

class AuthActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "my_channel_id"
    }


    private val TAG = "AuthActivity" // Definimos el TAG para los logs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Continuar con el resto de la configuración
        setup()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notifications"
            val descriptionText = "Channel for my app notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

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
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Obtener el usuario autenticado
                        val user = task.result?.user
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

                                        // Solicitar el token de FCM después de que el usuario se haya autenticado
                                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                            if (!task.isSuccessful) {
                                                android.util.Log.w(
                                                    TAG,
                                                    "Fetching FCM registration token failed",
                                                    task.exception
                                                )
                                                return@addOnCompleteListener
                                            }

                                            // Obtener el nuevo token de registro
                                            val token = task.result
                                            android.util.Log.d(TAG, "Token FCM: $token")
                                            // Enviar el token al servidor usando TokenManager
                                            TokenManager.sendRegistrationToServer(token)
                                        }

                                        // Dirigir a la actividad correspondiente según esComercio
                                        actividadComercio(
                                            email,
                                            ProviderType.BASIC,
                                            esComercio,
                                            nombre,
                                            apellido
                                        )
                                    } else {
                                        showAlert() // Si el documento no existe
                                    }
                                }
                                .addOnFailureListener {
                                    showAlert() // Si hay un error al obtener el documento
                                }
                        }
                    } else {
                        showAlert() // Mostrar alerta si la autenticación falla
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

    private fun actividadComercio(
        email: String,
        provider: ProviderType,
        esComercio: Boolean,
        nombre: String,
        apellido: String
    ) {
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


package com.appmoviles.utadeliciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {

    private lateinit var ivUsuario: ImageView
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val storageReference = FirebaseStorage.getInstance().reference

    private lateinit var tvSeleccionaFoto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Inicializa las vistas correctamente después de setContentView
        tvSeleccionaFoto = findViewById(R.id.tvEscogerImagen)
        ivUsuario = findViewById(R.id.ivUsuario)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase Completa")
        analytics.logEvent("InitScreen", bundle)

        setup()

        ivUsuario.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
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
                        val esComercio = switchComercio.isChecked

                        if (imageUri != null) {
                            val imageRef = storageReference.child("users/$userId/profile.jpg")
                            imageRef.putFile(imageUri!!)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                                            saveUserData(userId, uri.toString(), esComercio)
                                        }
                                    } else {
                                        Log.w("Firebase Storage", "Error al subir la imagen", task.exception)
                                        showAlert()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firebase Storage", "Error al subir la imagen", e)
                                    showAlert()
                                }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data?.data != null) {
                imageUri = data.data
                ivUsuario.setImageURI(imageUri)
                tvSeleccionaFoto.visibility = View.GONE
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data?.extras != null) {
                val imageBitmap = data.extras?.get("data") as? Bitmap
                ivUsuario.setImageBitmap(imageBitmap)
                tvSeleccionaFoto.visibility = View.GONE
            }
        }
    }

    private fun saveUserData(userId: String?, imageUrl: String?, esComercio: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val userData = hashMapOf(
            "nombre" to findViewById<EditText>(R.id.nameEditText).text.toString(),
            "apellido" to findViewById<EditText>(R.id.lasNameEditText).text.toString(),
            "email" to findViewById<EditText>(R.id.emailEditText).text.toString(),
            "esComercio" to esComercio,
            "profileImageUrl" to imageUrl
        )

        userId?.let { uid ->
            db.collection("user-info").document(uid)
                .set(userData)
                .addOnSuccessListener {
                    showHome(
                        findViewById<EditText>(R.id.emailEditText).text.toString(),
                        ProviderType.BASIC,
                        findViewById<EditText>(R.id.nameEditText).text.toString(),
                        findViewById<EditText>(R.id.lasNameEditText).text.toString(),
                        esComercio,
                        imageUrl
                    )
                }
                .addOnFailureListener { e ->
                    showAlert()
                    Log.w("Firestore", "Error al guardar los datos", e)
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

    private fun showHome(
        email: String,
        provider: ProviderType,
        name: String,
        lastname: String,
        esComercio: Boolean,
        imageUrl: String?
    ) {
        if (esComercio) {
            // Si es comercio, redirigir a la actividad correspondiente para dueños de comercio
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
                putExtra("name", name)
                putExtra("lastname", lastname)
                putExtra("profileImageUrl",imageUrl)
            }
            startActivity(homeIntent)
        } else {
            // Si no es comercio, redirigir a la actividad con barra de navegación
            val homeIntent = Intent(this, NavCliente::class.java).apply {
                putExtra("email", email)
                putExtra("provider", provider.name)
                putExtra("name", name)
                putExtra("lastname", lastname)
                putExtra("profileImageUrl", imageUrl)
            }
            startActivity(homeIntent)
        }
    }
}

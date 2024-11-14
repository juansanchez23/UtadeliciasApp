package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val resetEmailEditText = findViewById<EditText>(R.id.resetEmailEditText)
        val sendResetButton = findViewById<Button>(R.id.sendResetButton)

        sendResetButton.setOnClickListener {
            val email = resetEmailEditText.text.toString()
            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Correo de restablecimiento enviado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, ingrese un correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

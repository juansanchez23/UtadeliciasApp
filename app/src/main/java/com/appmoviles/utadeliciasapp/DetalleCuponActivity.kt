package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetalleCuponActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cupon)

        // Recuperar los datos pasados a la actividad
        val nombre = intent.getStringExtra("nombre") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val imagenUrl = intent.getStringExtra("imagenUrl") ?: ""

        // Configurar las vistas
        findViewById<TextView>(R.id.tvNombreDetalle).text = nombre
        findViewById<TextView>(R.id.tvDescripcionDetalle).text = descripcion

        // Cargar la imagen usando Glide
        if (imagenUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .into(findViewById<ImageView>(R.id.ivImagenDetalle))
        }

        // Configurar el botón de regreso
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }
}
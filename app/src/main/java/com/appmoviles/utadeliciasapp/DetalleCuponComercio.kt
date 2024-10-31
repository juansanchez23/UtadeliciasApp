package com.appmoviles.utadeliciasapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

class DetalleCuponComercio : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cupon_comercio)

        // Recuperar los datos pasados a la actividad
        val nombre = intent.getStringExtra("nombre") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val imagenUrl = intent.getStringExtra("imagenUrl") ?: ""
        val cuponId = intent.getStringExtra("userId") ?: ""


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

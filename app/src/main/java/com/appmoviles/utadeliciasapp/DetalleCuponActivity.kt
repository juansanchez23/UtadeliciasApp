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
import android.view.View

class DetalleCuponActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cupon)

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

        val ivQr = findViewById<ImageView>(R.id.iv_qr)
        val buttonGenerate = findViewById<Button>(R.id.bt_generate)

        // Configurar el botón para generar el QR
        findViewById<Button>(R.id.bt_generate).setOnClickListener {
            generateQR("CUPON:$cuponId", ivQr)
            Log.d("MiEtiqueta", "Valor de la variable: $cuponId")
            Log.d("MiEtiqueta", "Valor de la variable: $descripcion")
            buttonGenerate.visibility = View.GONE
        }

        // Configurar el botón de regreso
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }

    private fun generateQR(text: String, imageView: ImageView) {
        val writer = MultiFormatWriter()
        try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 900, 900)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            imageView.setImageBitmap(bmp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

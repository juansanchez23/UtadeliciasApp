package com.appmoviles.utadeliciasapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator

class CrearQr : Fragment() {

    private lateinit var editInput: EditText
    private lateinit var btGenerate: Button
    private lateinit var ivQr: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_crear_qr, container, false)

        // Inicializar las vistas
        editInput = view.findViewById(R.id.edit_input)
        btGenerate = view.findViewById(R.id.bt_generate)
        ivQr = view.findViewById(R.id.iv_qr)

        // Asignar el listener al botón
        btGenerate.setOnClickListener {
            generateQR()
        }

        return view
    }

    private fun generateQR() {
        val text = "CUPON:" + editInput.text.toString().trim() // Agrega el prefijo
        val writer = MultiFormatWriter()
        println(text)
        try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 800, 800)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            ivQr.setImageBitmap(bmp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

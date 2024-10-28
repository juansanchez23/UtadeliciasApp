package com.appmoviles.utadeliciasapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*

class Scanner : Fragment() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(requireContext(), scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            // Callback de decodificación
            decodeCallback = DecodeCallback { result ->
                requireActivity().runOnUiThread {
                    val textoQR = result.text

                    Log.d("ScannerFragment", "Texto escaneado: $textoQR")

                    if (textoQR.startsWith("CUPON:")) {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, ConfirmacionExitosa())  // Asegúrate de usar el ID correcto
                            .addToBackStack(null)
                            .commit()
                    } else if (textoQR.startsWith("http://") || textoQR.startsWith("https://")) {
                        // Abrir el enlace automáticamente si es un URL válido
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(textoQR))
                        startActivity(intent)
                    } else {
                        // Copiar al portapapeles si no es un enlace
                        copiarAlPortapapeles(textoQR)
                    }
                }
            }

            // Callback de error
            errorCallback = ErrorCallback { error ->
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error al inicializar la cámara: ${error.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        scannerView.setOnClickListener { codeScanner.startPreview() }

        view.findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                codeScanner.zoom = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        checkPermission(android.Manifest.permission.CAMERA, 200)
    }

    private fun copiarAlPortapapeles(texto: String) {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Texto QR", texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun checkPermission(permission: String, reqCode: Int) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), reqCode)
        }
    }
}

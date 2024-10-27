package com.appmoviles.utadeliciasapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.budiyev.android.codescanner.CodeScanner

import android.widget.Toast
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback

class Scanner : Fragment() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)

        // Inicializa CodeScanner
        codeScanner = CodeScanner(requireContext(), scannerView)

        // Configura los parámetros
        codeScanner.camera = CodeScanner.CAMERA_BACK // o CAMERA_FRONT
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        // Inicia el escaneo cuando se toca el scannerView
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                codeScanner.zoom=progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Manejo cuando empieza a tocar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Manejo cuando deja de tocar
            }
        })

        checkPermission(android.Manifest.permission.CAMERA,200 )
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview() // Reinicia el preview al volver
    }

    override fun onPause() {
        codeScanner.releaseResources() // Libera recursos de la cámara al pausar
        super.onPause()
    }

    fun checkPermission(permission: String, reqCode: Int) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), reqCode)
        }
    }

}
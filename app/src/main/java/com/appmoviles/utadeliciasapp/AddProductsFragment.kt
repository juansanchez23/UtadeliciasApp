package com.appmoviles.utadeliciasapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class AddProductsFragment : Fragment() {

    // Definición de vistas y variables
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var ivProduct: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnAddProduct: Button
    private lateinit var ivBackAdd: ImageView
    private lateinit var etQuantity: EditText

    // Instancias de Firestore y Storage
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    // Variable para almacenar el bitmap de la imagen capturada
    private var imageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización de vistas
        etName = view.findViewById(R.id.etName)
        etDescription = view.findViewById(R.id.etDescription)
        ivProduct = view.findViewById(R.id.ivProduct)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnAddProduct = view.findViewById(R.id.btnAddProduct)
        ivBackAdd = view.findViewById(R.id.ivBack_add)
        etQuantity = view.findViewById(R.id.etQuantity)

        // Configuración del botón para capturar imagen
        btnSelectImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }


        // Configuración del botón para agregar producto
        btnSelectImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
        }

        btnAddProduct.setOnClickListener {
            val name = etName.text.toString()
            val description = etDescription.text.toString()
            val quantity = etQuantity.text.toString().toIntOrNull() ?: 0

            if (imageBitmap != null) {
                uploadImageToFirebase(imageBitmap!!) { imageUrl ->
                    saveProductToFirestore(name, description, quantity, imageUrl)
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }

        ivBackAdd.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            imageBitmap = extras?.get("data") as Bitmap
            ivProduct.setImageBitmap(imageBitmap)
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap, callback: (String) -> Unit) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        val imageRef = storage.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putBytes(imageData)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProductToFirestore(name: String, description: String, quantity: Int, imageUrl: String) {
        val product = hashMapOf(
            "Nombre" to name,
            "Descripción" to description,
            "Cantidad" to quantity,
            "ImagenUrl" to imageUrl
        )

        db.collection("Products")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Producto agregado con éxito", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etDescription.text.clear()
                etQuantity.text.clear()
                ivProduct.setImageBitmap(null)
                imageBitmap = null
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al agregar el producto", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Se requiere el permiso de la cámara",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
package com.appmoviles.utadeliciasapp

import android.app.Activity
import android.content.Intent
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
    private lateinit var ivBackAdd:ImageView
    private lateinit var etQuantity:EditText

    // Instancias de Firestore y Storage
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    // Variable para almacenar el bitmap de la imagen capturada
    private lateinit var imageBitmap: Bitmap

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
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }

        // Configuración del botón para agregar producto
        btnAddProduct.setOnClickListener {
            val nom = etName.text.toString()
            val des = etDescription.text.toString()
            val cantidad = etQuantity.text.toString().toIntOrNull() ?: 0 // Asigna 0 si el valor es nulo
            val imageUrl = imageBitmap.toString()

            val data = hashMapOf(
                "Nombre" to nom,
                "Descripción" to des,
                "Cantidad" to cantidad,
                "ImagenUrl" to imageUrl
            )
            db.collection("Products")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Manejar el error
                }

            // Verificación de que todos los campos estén completos
            if (nom.isNotEmpty() && des.isNotEmpty() && ::imageBitmap.isInitialized) {
                uploadImageToFirebase(imageBitmap) { imageUrl ->
                    saveProductToFirestore(nom, des, imageUrl)
                }
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos e incluye una imagen", Toast.LENGTH_SHORT).show()
            }
        }
        ivBackAdd.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    // Manejo del resultado de la actividad de captura de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            imageBitmap = extras?.get("data") as Bitmap
            ivProduct.setImageBitmap(imageBitmap)
        }
    }

    // Función para subir la imagen a Firebase Storage y obtener su URL
    private fun uploadImageToFirebase(bitmap: Bitmap, callback: (String) -> Unit) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        // Referencia para almacenar la imagen en Firebase Storage
        val imageRef = storage.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putBytes(imageData)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString()) // Llama al callback con la URL de la imagen
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para guardar los detalles del producto en Firestore
    private fun saveProductToFirestore(name: String, description: String, imageUrl: String) {
        val product = hashMapOf(
            "Nombre" to name,
            "Descripción" to description,
            "ImagenUrl" to imageUrl
        )

        db.collection("Products")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Producto agregado con éxito", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etDescription.text.clear()
                ivProduct.setImageBitmap(null) // Reiniciar la imagen
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al agregar el producto", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}

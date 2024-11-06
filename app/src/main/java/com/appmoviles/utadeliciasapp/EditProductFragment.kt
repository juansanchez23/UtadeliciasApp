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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class EditProductFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var ivProduct: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSaveChanges: Button
    private lateinit var etQuantity: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var etPrice: EditText
    private lateinit var ivBack:ImageView
    private var productQuantity: Int = 0
    private var productPrice: Double = 0.0

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private var imageBitmap: Bitmap? = null
    private var productId: String? = null
    private var currentImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            productId = it.getString("productId")
            currentImageUrl = it.getString("productImage")
            productQuantity = it.getInt("productQuantity", 0)
            productPrice = it.getDouble("productPrice", 0.0)
        }

        // Recibe los datos pasados desde ProductDetailFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_product, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.etName)
        etDescription = view.findViewById(R.id.etDescription)
        ivProduct = view.findViewById(R.id.ivProduct)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges)
        etQuantity = view.findViewById(R.id.etQuantity)
        progressBar = view.findViewById(R.id.progressBar)
        etPrice = view.findViewById(R.id.etPrice)

        arguments?.let {
            etName.setText(it.getString("productName"))
            etDescription.setText(it.getString("productDescription"))
            etQuantity.setText(it.getInt("productQuantity").toString())
            etPrice.setText(it.getDouble("productPrice").toString())
        }

        // Establece la imagen si hay una URL actual
        currentImageUrl?.let {
            Glide.with(this).load(it).into(ivProduct)
        }

        ivBack=view.findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSelectImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }

        btnSaveChanges.setOnClickListener {
            saveProductChanges()

        }
    }

    private fun saveProductChanges() {
        val name = etName.text.toString()
        val description = etDescription.text.toString()
        val quantity = etQuantity.text.toString().toIntOrNull() ?: 0
        val price = etPrice.text.toString().toIntOrNull() ?: 0

        progressBar.visibility = View.VISIBLE

        if (imageBitmap != null) {
            uploadImageToFirebase(imageBitmap!!) { imageUrl ->
                updateProductInFirestore()
            }
        } else {
            updateProductInFirestore()
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
                Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateProductInFirestore() {
        val newName = etName.text.toString()
        val newDescription = etDescription.text.toString()
        val newQuantity = etQuantity.text.toString().toIntOrNull() ?: productQuantity
        val newPrice = etPrice.text.toString().toDoubleOrNull() ?: productPrice // Aquí se mantiene el precio si el campo está vacío

        val productUpdates = hashMapOf<String, Any>(
            "Nombre" to newName,
            "Descripción" to newDescription,
            "Cantidad" to newQuantity,
            "Precio" to newPrice
        )

        productId?.let {
            db.collection("Products").document(it)
                .update(productUpdates as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, ProductosFragmentos())
                        .addToBackStack(null)
                        .commit()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
                }
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

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }
}

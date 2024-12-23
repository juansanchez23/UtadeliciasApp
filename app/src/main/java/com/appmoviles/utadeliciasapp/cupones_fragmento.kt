package com.appmoviles.utadeliciasapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class cupones_fragmento : Fragment(), AdaptadorCupones.OnItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val tuColeccion = db.collection("Cupones")
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptadorCupones
    private lateinit var viewModel: SharedCuponesViewModel
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private val auth = FirebaseAuth.getInstance()
    private lateinit var currentUserCuponesRef: CollectionReference
    private val CAMERA_PERMISSION_CODE = 1001
    private val GALLERY_PERMISSION_CODE = 1002
    private val CAMERA_REQUEST_CODE = 1003
    private val GALLERY_REQUEST_CODE = 1004
    private var currentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cupones_fragmento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser?.let { user ->
            currentUserCuponesRef = db
                .collection("usuarios_comercio")
                .document(user.uid)
                .collection("cupones")
        }

        viewModel = ViewModelProvider(requireActivity())[SharedCuponesViewModel::class.java]

        // Inicializar vistas
        imageView = view.findViewById(R.id.imageView)
        recyclerView = view.findViewById(R.id.rDatos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorCupones(this)
        recyclerView.adapter = adapter

        // Observar cambios en los cupones
        viewModel.userCupones.observe(viewLifecycleOwner) { cupones ->
            adapter.setDatos(cupones)
        }
        val btnAgregar: Button = view.findViewById(R.id.btnAgregarCupon)
        val btnUpdate : Button = view.findViewById(R.id.btnActualizar)
        val btnDelete : Button = view.findViewById(R.id.btnEliminar)

        btnDelete.setOnClickListener {
            val txt_nombre : TextView = requireView().findViewById(R.id.txt_Nombre)
            val txt_descripcion : TextView =  requireView().findViewById(R.id.txt_Descripcion)
            val txt_id: TextView = requireView().findViewById(R.id.textId)
            val IDD: String = txt_id.text.toString().trim()
            var nom : String = txt_nombre.text.toString()
            var des : String = txt_descripcion.text.toString()



            // Validación 1: Verificar si el campo de ID está vacío
            if (IDD.isEmpty()) {
                Toast.makeText(requireContext(), "El campo de ID está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación 2: Verificar si el ID es válido
            if (IDD.length < 5) {
                Toast.makeText(requireContext(), "El ID del cupón no es válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Confirmación de eliminación
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación del cupón")
                .setMessage("¿Estás seguro de que deseas eliminar el cupón con ID: $IDD?")
                .setPositiveButton("Sí") { dialog, which ->
                    tuColeccion.document(IDD)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Cupón eliminado exitosamente", Toast.LENGTH_SHORT).show()
                            viewModel.getCuponesForCurrentUser()  // Refresh the data after adding


                            // Consultar la colección nuevamente después de la eliminación
                            consultarColeccion()

                            // Limpiar el campo de ID después de la eliminación
                            txt_id.text = ""
                            txt_nombre.text=""
                            txt_descripcion.text=""

                            // Verificar si no quedan cupones y limpiar la pantalla o los campos
                            tuColeccion.get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (querySnapshot.isEmpty) {
                                        // Limpiar los campos si no hay más cupones
                                        txt_id.text = ""
                                        txt_nombre.text = ""
                                        txt_id.text = ""
                                        Toast.makeText(requireContext(), "No quedan cupones", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al eliminar el cupón: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }

        btnUpdate.setOnClickListener{
            val txt_nombre : TextView = view.findViewById(R.id.txt_Nombre)
            val txt_id : TextView = requireView().findViewById(R.id.textId)

            val txt_descripcion : TextView = view.findViewById(R.id.txt_Descripcion)
            var nom : String = txt_nombre.text.toString()
            var des : String = txt_descripcion.text.toString()
            var IDD : String = txt_id.text.toString()

            val docActualizado = hashMapOf<String ,Any>()
            docActualizado["Nombre"]=nom
            docActualizado["Descripcion"]=des
            tuColeccion.document(IDD)

                .update(docActualizado)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Actualización Exitosa :)", Toast.LENGTH_SHORT).show()
                    consultarColeccion()
                    viewModel.getCuponesForCurrentUser()  // Refresh the data after adding

                }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "Error, intentalo de nuevo " , Toast.LENGTH_SHORT).show()

                }

        }




        btnAgregar.setOnClickListener()
        {
            val db= FirebaseFirestore.getInstance()
            val txt_nombre : TextView = view.findViewById(R.id.txt_Nombre)
            val txt_descripcion : TextView = view.findViewById(R.id.txt_Descripcion)
            var nom : String = txt_nombre.text.toString()
            var des : String = txt_descripcion.text.toString()
            val data = hashMapOf(
                "Nombre" to nom,
                "Descripcion" to des)
            db.collection("Cupones")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(requireContext(), "Registro del cupón Exitoso <3", Toast.LENGTH_SHORT).show()
                    consultarColeccion()
                    viewModel.getCuponesForCurrentUser()  // Refresh the data after adding

                }

                .addOnFailureListener { e ->

                }

        }




        // Configurar botones
        setupButtons(view)
    }

    private fun setupButtons(view: View) {
        // Botón seleccionar imagen
        val btnSelectImage: Button = view.findViewById(R.id.btnSelectImage)
        btnSelectImage.setOnClickListener {
            showImageSourceDialog()
        }

        // Botón agregar
        val btnAgregar: Button = view.findViewById(R.id.btnAgregarCupon)
        btnAgregar.setOnClickListener {
            auth.currentUser?.let { user ->
                uploadImageAndDataForUser(user.uid)
            } ?: run {
                Toast.makeText(requireContext(), "Debes iniciar sesión como comercio", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón actualizar
        val btnUpdate: Button = view.findViewById(R.id.btnActualizar)
        btnUpdate.setOnClickListener {
            updateCupon(view)
        }

        // Botón eliminar
        val btnDelete: Button = view.findViewById(R.id.btnEliminar)
        btnDelete.setOnClickListener {
            deleteCupon()
        }
    }
    private fun showImageSourceDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar de galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }



    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(requireContext(), "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show()
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.appmoviles.utadeliciasapp.fileprovider",
                        it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
            GALLERY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(requireContext(), "Permiso de galería denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    currentPhotoPath?.let { path ->
                        imageUri = Uri.fromFile(File(path))
                        Glide.with(requireContext())
                            .load(imageUri)
                            .into(imageView)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        imageUri = uri
                        Glide.with(requireContext())
                            .load(imageUri)
                            .into(imageView)
                    }
                }
            }
        }
    }


    private fun uploadImageAndDataForUser(userId: String) {
        val txt_nombre: TextView = requireView().findViewById(R.id.txt_Nombre)
        val txt_descripcion: TextView = requireView().findViewById(R.id.txt_Descripcion)
        val nom: String = txt_nombre.text.toString()
        val des: String = txt_descripcion.text.toString()

        if (imageUri == null) {
            Toast.makeText(requireContext(), "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Subiendo... espera un momento")
        progressDialog.show()

        val imageRef = storageRef.child("usuarios_comercio/$userId/cupones/${System.currentTimeMillis()}_${imageUri!!.lastPathSegment}")

        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val data = hashMapOf(
                        "Nombre" to nom,
                        "Descripcion" to des,
                        "imagenUrl" to uri.toString(),
                        "userId" to userId,
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    currentUserCuponesRef
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(), "Cupón agregado exitosamente", Toast.LENGTH_SHORT).show()
                            clearFields()
                            viewModel.getCuponesForCurrentUser()
                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(), "Error al guardar el cupón: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCupon(view: View) {
        auth.currentUser?.let { user ->
            val txt_nombre: TextView = view.findViewById(R.id.txt_Nombre)
            val txt_id: TextView = requireView().findViewById(R.id.textId)
            val txt_descripcion: TextView = view.findViewById(R.id.txt_Descripcion)
            val nom: String = txt_nombre.text.toString()
            val des: String = txt_descripcion.text.toString()
            val IDD: String = txt_id.text.toString()

            // Verificar si hay una nueva imagen seleccionada
            if (imageUri != null) {
                // Primero subir la nueva imagen
                val imageRef = storageRef.child("usuarios_comercio/${user.uid}/cupones/${System.currentTimeMillis()}_${imageUri!!.lastPathSegment}")

                val progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle("Actualizando... espera un momento")
                progressDialog.show()

                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Actualizar el documento con la nueva URL de la imagen
                            val docActualizado = hashMapOf<String, Any>(
                                "Nombre" to nom,
                                "Descripcion" to des,
                                "imagenUrl" to uri.toString(),
                                "updatedAt" to FieldValue.serverTimestamp()
                            )

                            currentUserCuponesRef
                                .document(IDD)
                                .update(docActualizado)
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(requireContext(), "Actualización Exitosa", Toast.LENGTH_SHORT).show()
                                    consultarColeccion()
                                    imageUri = null  // Limpiar la URI de la imagen después de actualizar
                                }
                                .addOnFailureListener { e ->
                                    progressDialog.dismiss()
                                    Toast.makeText(requireContext(), "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Si no hay nueva imagen, solo actualizar los campos de texto
                val docActualizado = hashMapOf<String, Any>(
                    "Nombre" to nom,
                    "Descripcion" to des,
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                currentUserCuponesRef
                    .document(IDD)
                    .update(docActualizado)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Actualización Exitosa", Toast.LENGTH_SHORT).show()
                        consultarColeccion()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun deleteCupon() {
        auth.currentUser?.let { user ->
            val txt_id: TextView = requireView().findViewById(R.id.textId)
            val IDD: String = txt_id.text.toString().trim()

            if (IDD.isEmpty()) {
                Toast.makeText(requireContext(), "El campo de ID está vacío", Toast.LENGTH_SHORT).show()
                return
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este cupón?")
                .setPositiveButton("Sí") { _, _ ->
                    currentUserCuponesRef
                        .document(IDD)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Cupón eliminado exitosamente", Toast.LENGTH_SHORT).show()
                            clearFields()
                            consultarColeccion()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al eliminar el cupón: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun clearFields() {
        requireView().apply {
            findViewById<TextView>(R.id.txt_Nombre).text = ""
            findViewById<TextView>(R.id.txt_Descripcion).text = ""
            findViewById<TextView>(R.id.textId).text = ""
            findViewById<ImageView>(R.id.imageView).setImageResource(0)
        }
        imageUri = null
    }

    private fun consultarColeccion() {
        auth.currentUser?.let { user ->
            currentUserCuponesRef
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val listaTuModelo = mutableListOf<Cupones>()
                    for (document in querySnapshot) {
                        val nombre = document.getString("Nombre")
                        val descripcion = document.getString("Descripcion")
                        val imagenUrl = document.getString("imagenUrl") ?: ""
                        val userId = document.getString("userId") ?: ""
                        val ID = document.id
                        if (nombre != null && descripcion != null) {
                            val tuModelo = Cupones(ID, nombre, descripcion, imagenUrl, userId)
                            listaTuModelo.add(tuModelo)
                        }
                    }
                    adapter.setDatos(listaTuModelo)
                    viewModel.getCuponesForCurrentUser()
                }
        }
    }

    override fun onItemClick(tuModelo: Cupones) {
        requireView().apply {
            findViewById<TextView>(R.id.txt_Nombre).text = tuModelo.nombre
            findViewById<TextView>(R.id.txt_Descripcion).text = tuModelo.descripcion
            findViewById<TextView>(R.id.textId).text = tuModelo.id

            // Cargar imagen si existe
            if (tuModelo.imagenUrl.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(tuModelo.imagenUrl)
                    .into(imageView)
            }
        }
    }
}
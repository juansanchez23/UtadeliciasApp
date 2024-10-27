package com.appmoviles.utadeliciasapp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cupones_fragmento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedCuponesViewModel::class.java]

        // Inicializar vistas
        imageView = view.findViewById(R.id.imageView)
        recyclerView = view.findViewById(R.id.rDatos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorCupones(this)
        recyclerView.adapter = adapter

        // Observar cambios en los cupones
        viewModel.cupones.observe(viewLifecycleOwner) { cupones ->
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
                            viewModel.getCupones()  // Refresh the data after adding


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
                    viewModel.getCupones()  // Refresh the data after adding

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
                    viewModel.getCupones()  // Refresh the data after adding

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
            selectImage()
        }

        // Botón agregar
        val btnAgregar: Button = view.findViewById(R.id.btnAgregarCupon)
        btnAgregar.setOnClickListener {
            uploadImageAndData()
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

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            try {
                Glide.with(requireContext())
                    .load(imageUri)
                    .into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageAndData() {
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

        val imageRef = storageRef.child("cupones_imagenes/${System.currentTimeMillis()}_${imageUri!!.lastPathSegment}")

        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val data = hashMapOf(
                        "Nombre" to nom,
                        "Descripcion" to des,
                        "imagenUrl" to uri.toString()
                    )

                    db.collection("Cupones")
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(), "Registro del cupón exitoso", Toast.LENGTH_SHORT).show()
                            clearFields()
                            consultarColeccion()
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
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                progressDialog.setMessage("Subiendo ${progress.toInt()}%")
            }
    }

    private fun updateCupon(view: View) {
        val txt_nombre: TextView = view.findViewById(R.id.txt_Nombre)
        val txt_id: TextView = requireView().findViewById(R.id.textId)
        val txt_descripcion: TextView = view.findViewById(R.id.txt_Descripcion)
        val nom: String = txt_nombre.text.toString()
        val des: String = txt_descripcion.text.toString()
        val IDD: String = txt_id.text.toString()

        val docActualizado = hashMapOf<String, Any>(
            "Nombre" to nom,
            "Descripcion" to des,
        )

        tuColeccion.document(IDD)
            .update(docActualizado)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Actualización Exitosa :)", Toast.LENGTH_SHORT).show()
                consultarColeccion()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error, inténtalo de nuevo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteCupon() {
        val txt_id: TextView = requireView().findViewById(R.id.textId)
        val IDD: String = txt_id.text.toString().trim()

        if (IDD.isEmpty()) {
            Toast.makeText(requireContext(), "El campo de ID está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (IDD.length < 5) {
            Toast.makeText(requireContext(), "El ID del cupón no es válido.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar el cupón con ID: $IDD?")
            .setPositiveButton("Sí") { _, _ ->
                tuColeccion.document(IDD)
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
        tuColeccion.get()
            .addOnSuccessListener { querySnapshot ->
                val listaTuModelo = mutableListOf<Cupones>()

                for (document in querySnapshot) {
                    val nombre = document.getString("Nombre")
                    val descripcion = document.getString("Descripcion")
                    val imagenUrl = document.getString("imagenUrl") ?: ""
                    val ID = document.id
                    if (nombre != null && descripcion != null) {
                        val tuModelo = Cupones(ID, nombre, descripcion, imagenUrl)
                        listaTuModelo.add(tuModelo)
                    }
                }
                adapter.setDatos(listaTuModelo)
                viewModel.getCupones()
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
package com.appmoviles.utadeliciasapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class cupones_fragmento : Fragment(), AdaptadorCupones.OnItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val tuColeccion = db.collection("Cupones")
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter :AdaptadorCupones
    private lateinit var viewModel: SharedCuponesViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cupones_fragmento, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedCuponesViewModel::class.java]


        // Ahora puedes acceder a la vista
        recyclerView = view.findViewById(R.id.rDatos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorCupones(this)
        recyclerView.adapter = adapter

        viewModel.cupones.observe(viewLifecycleOwner) { cupones ->
            adapter.setDatos(cupones)
        }
        val btnAgregar: Button = view.findViewById(R.id.btnAgregarCupon)
        val btnConsultar : Button = view.findViewById(R.id.btnRefrescar)
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
                .setTitle("Confirmar eliminación")
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
        btnConsultar.setOnClickListener()
        {
            consultarColeccion()
            viewModel.getCupones()  // Refresh the data after adding

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



    }

    private fun consultarColeccion()
    {
        tuColeccion.get()
            .addOnSuccessListener { querySnapshot ->
                val listaTuModelo = mutableListOf<Cupones>()
                for (document in querySnapshot)
                {


                        val nombre = document.getString("Nombre")
                        val descripcion = document.getString("Descripcion")
                        val ID = document.id
                        if (nombre != null  && descripcion != null)
                        {
                            val tuModelo =Cupones(ID,nombre,descripcion)
                            listaTuModelo.add((tuModelo))
                        }
                }

                adapter.setDatos(listaTuModelo)
                viewModel.getCupones()  // Refresh the data after adding

            }
    }

    override fun onItemClick(tuModelo: Cupones) {
        val txt_nombre : TextView = requireView().findViewById(R.id.txt_Nombre)
        val txt_descripcion : TextView =  requireView().findViewById(R.id.txt_Descripcion)
        val txt_id : TextView = requireView().findViewById(R.id.textId)
        txt_nombre.text=tuModelo.nombre
        txt_descripcion.text=tuModelo.descripcion
        txt_id.text=tuModelo.id



    }
}
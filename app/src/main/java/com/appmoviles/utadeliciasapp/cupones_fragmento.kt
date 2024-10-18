package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore


class cupones_fragmento : Fragment(), AdaptadorCupones.OnItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val tuColeccion = db.collection("Cupones")
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter :AdaptadorCupones

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cupones_fragmento, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ahora puedes acceder a la vista
        recyclerView = view.findViewById(R.id.rDatos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorCupones(this)
        recyclerView.adapter = adapter
        val btnConsultar : Button = view.findViewById(R.id.btnAgregarCupon)


        val btnEliminar : Button = view.findViewById(R.id.btnEliminarCupon)


        btnEliminar.setOnClickListener()
        {
            consultarColeccion()
        }

        btnConsultar.setOnClickListener()
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
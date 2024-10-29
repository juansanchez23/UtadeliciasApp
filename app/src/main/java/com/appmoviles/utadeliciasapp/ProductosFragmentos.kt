package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class ProductosFragmentos : Fragment() {

    private lateinit var btnAddProduct: Button
    private lateinit var btnDelProduct: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductsAdapter


    private val db =FirebaseFirestore.getInstance()
    private val ProductosColeccion= db.collection("Products")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el diseño y devolvemos la vista raíz
        return inflater.inflate(R.layout.fragment_productos_fragmentos, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnAddProduct = view.findViewById(R.id.btnaddProduct)
        btnDelProduct = view.findViewById(R.id.btnDelProduct)
        recyclerView = view.findViewById(R.id.rvproducts)
        recyclerView.layoutManager=LinearLayoutManager(requireContext())


        btnAddProduct.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, AddProductsFragment())
                .addToBackStack(null)
                .commit()
        }

        btnDelProduct.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DeleteProductFragment())
                .addToBackStack(null)
                .commit()
        }


}}

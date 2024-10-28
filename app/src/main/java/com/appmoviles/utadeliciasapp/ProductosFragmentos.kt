package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductosFragmentos : Fragment() {

    private lateinit var btnAddProduct: Button
    private lateinit var btnDelProduct: Button
    //private lateinit var ivBack_add: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el diseño y devolvemos la vista raíz
        return inflater.inflate(R.layout.fragment_productos_fragmentos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar los botones y el ImageView de retroceso
        btnAddProduct = view.findViewById(R.id.btnaddProduct)
        btnDelProduct = view.findViewById(R.id.btnDelProduct)
        //ivBack_add= view.findViewById(R.id.ivBack_add)

        // Configurar los listeners de los botones
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

        // Configurar el listener del ImageView para retroceder

}}

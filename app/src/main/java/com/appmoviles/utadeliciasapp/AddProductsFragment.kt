
package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class AddProductsFragment : Fragment() {

    private lateinit var ivBack_add: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño para este fragmento
        return inflater.inflate(R.layout.fragment_add_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el ImageView de retroceso
        ivBack_add = view.findViewById(R.id.ivBack_add)

        // Configurar el listener para el ImageView
        ivBack_add.setOnClickListener {
            parentFragmentManager.popBackStack() // Vuelve al fragmento anterior en la pila
        }
    }
}

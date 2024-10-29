package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appmoviles.utadeliciasapp.databinding.ActivityNavegacionBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton




class fragment_inicio_fragmento : Fragment() {
    private lateinit var viewModel: SharedCuponesViewModel
    private lateinit var adapter: AdaptadorCupones

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inicio_fragmento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SharedCuponesViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.rDatosInicio)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorCupones(object : AdaptadorCupones.OnItemClickListener {
            override fun onItemClick(cupon: Cupones) {
                // No se necesita implementar nada aquí, ya que la lógica se encuentra en el adaptador
            }
        })
        recyclerView.adapter = adapter

        viewModel.userCupones.observe(viewLifecycleOwner) { cupones ->
            adapter.setDatos(cupones)
        }

        // Asegúrate de que los datos se carguen cuando se crea el fragmento
        viewModel.getCuponesForCurrentUser()

        val botonScanner: FloatingActionButton = view.findViewById(R.id.boton_scanner)
        botonScanner.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, Scanner())  // Asegúrate de usar el ID correcto
                .addToBackStack(null)
                .commit()
        }

    }
}

package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class home_cliente : Fragment() {
    private lateinit var viewModel: SharedCuponesViewModel
    private lateinit var adapter: AdaptadorCupones


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SharedCuponesViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.rDatosInicio)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorCupones(object : AdaptadorCupones.OnItemClickListener {
            override fun onItemClick(cupon: Cupones) {
                // Manejar el clic si es necesario
            }
        })
        recyclerView.adapter = adapter

        viewModel.cupones.observe(viewLifecycleOwner) { cupones ->
            adapter.setDatos(cupones)
        }

        // Asegúrate de que los datos se carguen cuando se crea el fragmento
        viewModel.getCuponesForCurrentUser()
}}
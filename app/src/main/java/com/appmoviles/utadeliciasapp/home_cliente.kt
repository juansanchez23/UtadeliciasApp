package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class home_cliente : Fragment() {
    private lateinit var viewModel: SharedCuponesViewModel
    private lateinit var adapter: AdaptadorClienteCupon

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SharedCuponesViewModel::class.java]
        val recyclerView = view.findViewById<RecyclerView>(R.id.rDatosInicio)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorClienteCupon(object : AdaptadorCupones.OnItemClickListener {
            override fun onItemClick(cupon: Cupones) {
            }
        })
        recyclerView.adapter = adapter

        viewModel.allCupones.observe(viewLifecycleOwner) { cupones ->
            adapter.setDatos(cupones)
        }


        viewModel.getAllCupones()

    }
}
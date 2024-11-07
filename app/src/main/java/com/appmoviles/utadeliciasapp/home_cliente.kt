package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.widget.Toast

class home_cliente: Fragment() {
    private lateinit var viewModel: SharedCuponesViewModel
    private lateinit var adapter: AdaptadorClienteCupon
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_cliente, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(requireActivity())[SharedCuponesViewModel::class.java]


        // Configurar RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rDatosInicio)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdaptadorClienteCupon(object : AdaptadorClienteCupon.OnItemClickListener {
            override fun onItemClick(cupon: Cupones) {
            }
        })
        recyclerView.adapter = adapter

        // Configurar SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            Toast.makeText(context, "Actualizando cupones...", Toast.LENGTH_SHORT).show()
        }

        // Observar cambios en los datos
        viewModel.allCupones.observe(viewLifecycleOwner) { cupones ->
            adapter.setDatos(cupones)
            swipeRefreshLayout.isRefreshing = false
        }

        // Cargar datos iniciales
        refreshData()
    }

    private fun refreshData() {
        swipeRefreshLayout.isRefreshing = true
        viewModel.getAllCupones()
    }
}
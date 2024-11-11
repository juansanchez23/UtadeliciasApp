import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.appmoviles.utadeliciasapp.NotificacionPedido
import com.appmoviles.utadeliciasapp.NotificacionesAdapter
import com.appmoviles.utadeliciasapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificacionesComercioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificacionesAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notificaciones_comercio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar RecyclerView y adapter
        recyclerView = view.findViewById(R.id.rvNotificaciones)
        adapter = NotificacionesAdapter()
        recyclerView.adapter = adapter

        // Log para verificar que el RecyclerView se inicializó
        Log.d("NotificacionesFragment", "RecyclerView inicializado")

        // Obtener el comercio_id actual
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Activity.MODE_PRIVATE)
        val comercioId = sharedPreferences.getString("comercio_id", null)

        // Log para verificar el comercio_id
        Log.d("NotificacionesFragment", "comercio_id: $comercioId")

        if (comercioId != null) {
            // Primero, verificar si hay documentos que coincidan con la consulta
            firestore.collection("pedidos")
                .whereEqualTo("productos.comercio_id", comercioId)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("NotificacionesFragment", "Número de documentos encontrados: ${documents.size()}")
                    if (documents.isEmpty) {
                        Toast.makeText(requireContext(), "No hay pedidos para mostrar", Toast.LENGTH_SHORT).show()
                    }
                }

            // Escuchar cambios en tiempo real de los pedidos
            firestore.collection("pedidos")
                .whereEqualTo("productos.comercio_id", comercioId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("NotificacionesFragment", "Error al escuchar pedidos", e)
                        Toast.makeText(requireContext(), "Error al cargar pedidos: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    Log.d("NotificacionesFragment", "SnapshotListener activado")

                    if (snapshots != null) {
                        Log.d("NotificacionesFragment", "Número de snapshots: ${snapshots.size()}")

                        val notificaciones = mutableListOf<NotificacionPedido>()
                        for (doc in snapshots) {
                            try {
                                Log.d("NotificacionesFragment", "Procesando documento: ${doc.id}")
                                val pedido = doc.toObject(NotificacionPedido::class.java)
                                pedido.pedidoId = doc.id // Asegurar que el ID se asigne

                                // Log para ver los productos antes del filtrado
                                Log.d("NotificacionesFragment", "Productos en pedido: ${pedido.productos}")

                                // Filtrar los productos por comercio_id
                                val productosFiltrados = pedido.productos.filter { it.comercio_id == comercioId }

                                if (productosFiltrados.isNotEmpty()) {
                                    // Crear una nueva notificación con los productos filtrados
                                    val notificacionFiltrada = pedido.copy(productos = productosFiltrados)
                                    notificaciones.add(notificacionFiltrada)
                                    Log.d("NotificacionesFragment", "Notificación agregada: ${notificacionFiltrada}")
                                }
                            } catch (e: Exception) {
                                Log.e("NotificacionesFragment", "Error al procesar documento", e)
                            }
                        }

                        Log.d("NotificacionesFragment", "Notificaciones a mostrar: ${notificaciones.size}")
                        adapter.actualizarNotificaciones(notificaciones)
                    }
                }
        } else {
            Log.e("NotificacionesFragment", "comercio_id es null")
            Toast.makeText(requireContext(), "Error: No se encontró el ID del comercio", Toast.LENGTH_SHORT).show()
        }
    }
}
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appmoviles.utadeliciasapp.NotificacionPedido
import com.appmoviles.utadeliciasapp.NotificacionesAdapter
import com.appmoviles.utadeliciasapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificacionesComercioFragment : Fragment() {

    private lateinit var rvNotificaciones: RecyclerView
    private lateinit var notificacionesAdapter: NotificacionesAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences // Cambio aquí

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializar SharedPreferences aquí
        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_notificaciones_comercio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNotificaciones = view.findViewById(R.id.rvNotificaciones)
        rvNotificaciones.layoutManager = LinearLayoutManager(requireContext())

        setupAdapter()
        cargarNotificaciones()
    }

    private fun setupAdapter() {
        notificacionesAdapter = NotificacionesAdapter(
            onAccept = { pedido ->
                aceptarPedido(pedido)
            },
            onReject = { pedido ->
            }
        )
        rvNotificaciones.adapter = notificacionesAdapter
    }

    private fun cargarNotificaciones() {
        val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("pedidos")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, "Error al cargar notificaciones", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notificaciones = mutableListOf<NotificacionPedido>()
                    for (document in snapshot.documents) {
                        document.toObject(NotificacionPedido::class.java)?.let { pedido ->
                            // Verificamos si algún producto del pedido tiene el comercio_id del usuario actual
                            if (pedido.productos.any { it.comercio_id == usuarioActual }) {
                                notificaciones.add(pedido)
                            }
                        }
                    }

                    if (notificaciones.isNotEmpty()) {
                        notificacionesAdapter.setData(notificaciones, usuarioActual)
                    } else {
                        Toast.makeText(context, "No se encontraron pedidos para este comercio", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No se encontraron pedidos", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun aceptarPedido(pedido: NotificacionPedido) {
        firestore.collection("pedidos").document(pedido.pedidoId)
            .update("estado", "enviado")  // Cambiado de "aceptado" a "enviado"
            .addOnSuccessListener {
                Toast.makeText(context, "Pedido enviado", Toast.LENGTH_SHORT).show()
                // Aquí podrías agregar una llamada para notificar al cliente de la actualización si es necesario.
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al enviar el pedido", Toast.LENGTH_SHORT).show()
            }
    }


}
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
import com.google.firebase.firestore.ListenerRegistration
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat

class NotificacionesComercioFragment : Fragment() {

    private lateinit var rvNotificaciones: RecyclerView
    private lateinit var notificacionesAdapter: NotificacionesAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    private var notificacionesListener: ListenerRegistration? = null


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

        notificacionesListener = firestore.collection("pedidos")
            .addSnapshotListener { snapshot, exception ->
                if (!isAdded) return@addSnapshotListener  // Asegúrate de que el fragmento está adjunto

                if (exception != null) {
                    mostrarMensaje("Error al cargar notificaciones")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notificaciones = mutableListOf<NotificacionPedido>()
                    for (document in snapshot.documents) {
                        document.toObject(NotificacionPedido::class.java)?.let { pedido ->
                            if (pedido.productos.any { it.comercio_id == usuarioActual }) {
                                notificaciones.add(pedido)

                                // Llamamos a la notificación cuando hay un nuevo pedido
                                mostrarNotificacionPedido(requireContext())
                            }
                        }
                    }

                    if (notificaciones.isNotEmpty()) {
                        notificacionesAdapter.setData(notificaciones, usuarioActual)
                    } else {
                        mostrarMensaje("No se encontraron pedidos para este comercio")
                    }
                } else {
                    mostrarMensaje("No se encontraron pedidos")
                }
            }
    }

    private fun mostrarMensaje(mensaje: String) {
        // Verificar si el Fragment está adjunto a una Activity y si es seguro mostrar el mensaje
        if (isAdded) {
            context?.let { ctx ->
                Toast.makeText(ctx, mensaje, Toast.LENGTH_SHORT).show()
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
    override fun onDestroyView() {
        // Remover el listener cuando el Fragment se destruye
        super.onDestroyView()

        notificacionesListener?.remove()
    }



    private fun mostrarNotificacionPedido(context: Context) {
        // ID único para el canal de notificación
        val channelId = "pedido_nuevo_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación (necesario en Android Oreo y versiones posteriores)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pedidos Nuevos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.cabitonormal)
            .setContentTitle("Pedido nuevo")
            .setContentText("Tienes un pedido nuevo.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }

}
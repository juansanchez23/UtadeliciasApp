
package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


class AddProductsFragment : Fragment() {

    private lateinit var ivBackAdd: ImageView
    private lateinit var btnAddProduct: Button
    lateinit var etDescription:EditText
    lateinit var etName:EditText
    private lateinit var btnSelectImage: Button
    private lateinit var ivProduct: ImageView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño para este fragmento
        return inflater.inflate(R.layout.fragment_add_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivBackAdd = view.findViewById(R.id.ivBack_add)
        btnAddProduct = view.findViewById(R.id.btnAddProduct)
        etDescription = view.findViewById(R.id.etDescription)
        etName = view.findViewById(R.id.etName)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        ivProduct = view.findViewById(R.id.ivProduct)



        btnAddProduct.setOnClickListener {
            val db=FirebaseFirestore.getInstance()
            var nom: String = etName.text.toString()
            var des : String = etDescription.text.toString()

            val data = hashMapOf(
                "Nombre" to nom,
                "Descripción" to des
            )
            db.collection("Products")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{ e -> }


        }
        ivBackAdd.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}

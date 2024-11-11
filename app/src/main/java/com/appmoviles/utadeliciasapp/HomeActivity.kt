package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.appmoviles.utadeliciasapp.databinding.ActivityNavegacionBinding

class HomeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNavegacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavegacionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        val name = bundle?.getString("name")
        val lastname = bundle?.getString("lastname")
        replaceFragment(fragment_inicio_fragmento())
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> replaceFragment(fragment_inicio_fragmento())
                R.id.notification -> replaceFragment(NotificacionesComercioFragment())
                R.id.productos -> replaceFragment(ProductosFragmentos())
                R.id.descuentos -> replaceFragment(cupones_fragmento())
                R.id.settings -> replaceFragment(ajustes_fragmento.newInstance(email ?: "", provider ?: "", name ?: "",lastname ?:""))
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}

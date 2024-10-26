package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.appmoviles.utadeliciasapp.databinding.ActivityNavegacionBinding

class Navegacion : AppCompatActivity() {

    private lateinit var binding: ActivityNavegacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavegacionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        replaceFragment(inicio_fragmento())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.home -> replaceFragment(inicio_fragmento())
                R.id.notification -> replaceFragment(notificaciones_fragmento())
                R.id.productos -> replaceFragment(productos_fragmentos())
                R.id.descuentos -> replaceFragment(cupones_fragmento())
                R.id.settings -> replaceFragment(ajustes_fragmento())

                else ->{

                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){

        val fragmentManager= supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}
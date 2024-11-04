package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.appmoviles.utadeliciasapp.databinding.ActivityNavCliente2Binding

class NavCliente : AppCompatActivity() {
    private lateinit var binding2: ActivityNavCliente2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2=ActivityNavCliente2Binding.inflate(layoutInflater)
        setContentView(binding2.root)
        replaceFragment2(home_cliente())
        binding2.bottomNavigationView2.selectedItemId = R.id.home_cliente

        binding2.bottomNavigationView2.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_cliente -> replaceFragment2(home_cliente())
                R.id.productos_cliente -> replaceFragment2(productos_cliente())
                R.id.carrito -> replaceFragment2(CarritoCliente())
                R.id.settings_cliente -> replaceFragment2(ajustes_cliente())
                R.id.notification_cliente -> replaceFragment2(notificacion_cliente())
                else ->{

                }
            }
            true
        }
    }

    private fun replaceFragment2(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction2=fragmentManager.beginTransaction()
        fragmentTransaction2.replace(R.id.navcliente,fragment)
        fragmentTransaction2.commit()

    }
}
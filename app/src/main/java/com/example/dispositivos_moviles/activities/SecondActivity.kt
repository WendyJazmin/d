package com.example.dispositivos_moviles.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.databinding.ActivityMainBinding
import com.example.dispositivos_moviles.databinding.ActivitySecondBinding
import com.example.dispositivos_moviles.fragments.FirstFragment
import com.example.dispositivos_moviles.fragments.SecondFragment
import com.example.dispositivos_moviles.fragments.ThirdFragment
import com.google.android.material.snackbar.Snackbar

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        var name : String = ""
//        intent.extras.let {
//            name = it?.getString("var1")!!
//        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.inicio -> {

                    val frag = FirstFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(binding.frmContainer.id, frag)
                    //cada clic que se haga se añade un fragment a la pila de navegacion del proyecto
                    transaction.addToBackStack(null)
                    transaction.commit()
                    // Respond to navigation item 1 click
                    true
                }
                R.id.favoritos -> {
                    // Respond to navigation item 2 click
                    val frag = SecondFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(binding.frmContainer.id, frag)
                    //cada clic que se haga se añade un fragment a la pila de navegacion del proyecto
                    transaction.addToBackStack(null)
                    transaction.commit()
                    // Respond to navigation item 1 click
                    true
                }
                R.id.apis -> {
                    // Respond to navigation item 2 click
                    val frag = ThirdFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(binding.frmContainer.id, frag)
                    //cada clic que se haga se añade un fragment a la pila de navegacion del proyecto
                    transaction.addToBackStack(null)
                    transaction.commit()
                    // Respond to navigation item 1 click
                    true
                }
                else -> false
            }
        }

        initClass()
    }

    fun initClass(){
        /*Log.d("uce", "Entrando a start")  debug en la terminal*/
        binding.btnRetorno.setOnClickListener{
            Log.d("UCE", "Entrando al click de retorno")
            var intent= Intent(this, ActivityMainBinding::class.java)
            startActivity(intent)

            /*Snackbar.make(
                binding.loginSegundo,"regresando",
                Snackbar.LENGTH_LONG).show()*/
        }
    }
}
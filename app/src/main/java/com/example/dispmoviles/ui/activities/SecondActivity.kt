package com.example.dispmoviles.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
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
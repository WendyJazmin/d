package com.example.dispmoviles.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dispmoviles.R
import com.example.dispmoviles.databinding.ActivityMainBinding
import com.example.dispmoviles.logic.validator.LoginValidator
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart(){
        super.onStart()
        initClass()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initClass(){
        Log.d("UCE", "Entrando a start")

        binding.buttonIngresar.setOnClickListener {
            Log.d("UCE", "Entrando al click")

            val check = LoginValidator().checklogin(
                binding.editTextTextEmailAddress.text.toString(),
                binding.editTextTextPassword.text.toString()
            )

            if(check) {
                var intent = Intent(this, SecondActivity::class.java)
                //intent.putExtra("var1", binding.editTextTextEmailAddress.toString())
                startActivity(intent)
                Snackbar.make(binding.textRegistrese ,"Correcto",
                    Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(binding.textRegistrese ,"usuario o contrasenia invalidos",
                    Snackbar.LENGTH_LONG).show()

            }
        }
    }
}
package com.example.dispositivos_moviles.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.dispositivos_moviles.databinding.ActivityMainBinding
import com.example.dispositivos_moviles.logic.validator.LoginValidator
import com.google.android.material.snackbar.Snackbar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {

    //sabado 15 de julio
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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

                //sabado 15 de julio
                lifecycleScope.launch(Dispatchers.IO){
                    saveDataStore(binding.editTextTextEmailAddress.text.toString())
                }
                //

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


    //sabado 15 de julio
    private suspend fun saveDataStore(stringData:String){
        dataStore.edit {prefs->//hace una funcion suspendida y se tiene que ejecutar en una corrutina
            prefs[stringPreferencesKey("usuario")]= stringData
            prefs[stringPreferencesKey("session")]= UUID.randomUUID().toString() //UUI universal User Identifier
            prefs[stringPreferencesKey("email")]= "dispomoviles@uce.edu.ec"
        }
    }
}
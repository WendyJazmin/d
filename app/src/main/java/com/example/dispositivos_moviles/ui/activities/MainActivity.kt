package com.example.dispositivos_moviles.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.lifecycle.lifecycleScope
import com.example.dispositivos_moviles.databinding.ActivityMainBinding
import com.example.dispositivos_moviles.ui.validator.LoginValidator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//imports sabado 15 de julio
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.util.UUID

//imports lunes 17 de julio
import android.app.SearchManager
import androidx.activity.result.contract.ActivityResultContracts

//sabado 15 de julio
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
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

        //Lunes 17 de julio
        binding.buttonTwitter.setOnClickListener{
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:-0.200628,-78.5786066"))
//             startActivity(intent)//no se que app va a abrir la url
            //ACTION_SEARCH
            val intentX = Intent(Intent.ACTION_WEB_SEARCH)
            intentX.setClassName("com.google.android.googlequicksearchbox",
                "com.google.android.googlequicksearchbox.SearchActivity")
            intentX.putExtra(SearchManager.QUERY,"UCE")

            startActivity(intentX)
        }

        val appResultLocal= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultActivity->
            when(resultActivity.resultCode){
                RESULT_OK ->{
                    Log.d("UCE","Resultado exitoso")
                    Snackbar.make(binding.textCorreo,"Resultadp exitoso",Snackbar.LENGTH_LONG).show()
                }

                RESULT_CANCELED->{Log.d("UCE","Resultado fallido")
                    Snackbar.make(binding.textCorreo,"Resultadp fallido",Snackbar.LENGTH_LONG).show()
                }
                else->{Log.d("UCE","Resultado dudoso" +
                        "")

                }
            }

        }
        binding.buttonFacebook.setOnClickListener{
            val resIntent = Intent(this,ResultActivity::class.java)
            appResultLocal.launch(resIntent)
        }
        //
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
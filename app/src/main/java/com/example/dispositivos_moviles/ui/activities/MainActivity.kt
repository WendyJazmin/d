package com.example.dispositivos_moviles.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

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
import com.example.dispositivos_moviles.R

//imports martes 18 de julio
import android.speech.RecognizerIntent
import java.util.Locale

import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

//
import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.core.content.PermissionChecker.PermissionResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import android.content.res.Resources
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission

//sabado 15 de julio
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //interfaz que nos va a permitir acceder a la ubicacion del ususario
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStart(){
        super.onStart()
        initClass()
    }

    @SuppressLint("MissingPermission")

    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private fun initClass(){
        Log.d("UCE", "Entrando a start")

        binding.buttonIngresar.setOnClickListener {
            Log.d("UCE", "Entrando al click")

            val check = LoginValidator().checklogin(
                binding.editTextEmailAddress.text.toString(),
                binding.editTextTextPassword.text.toString()
            )

            if(check) {

                //sabado 15 de julio
                lifecycleScope.launch(Dispatchers.IO){
                    saveDataStore(binding.editTextEmailAddress.text.toString())
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

        val locationContract = registerForActivityResult(RequestPermission()) {
                isGranted ->
            when(isGranted){
                true -> {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        it.longitude
                        it.latitude

                        val a = Geocoder(this)
                        a.getFromLocation(it.latitude, it.longitude, 1)
                    }

                    /*val task = fusedLocationProviderClient.lastLocation
                    task.addOnSuccessListener {
                        if(task.result != null) {
                            Snackbar.make(binding.txtName,
                                "${it.latitude}, ${it.longitude}",
                                Snackbar.LENGTH_LONG)
                                .show()
                        } else {
                            Snackbar.make(binding.txtName,
                                "Encienda el GPS, por favor",
                                Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }*/
                }

                //Informa al usuario de porque se necesita los permisos
                shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    Snackbar.make(binding.textCorreo,
                        "Ayude con el permiso",
                        Snackbar.LENGTH_LONG)
                        .show()
                }
                false -> {
                    Snackbar.make(binding.textCorreo, "Permiso denegado", Snackbar.LENGTH_LONG)
                        .show()
                }
            }

        }

        //Lunes 17 de julio
        binding.btnTwitter.setOnClickListener {

            locationContract.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            /*val intent = Intent(
                Intent.ACTION_WEB_SEARCH
            )
            //abre la barra de busqueda de Google
            intent.setClassName(
                "com.google.android.googlequicksearchbox",
                "com.google.android.googlequicksearchbox.SearchActivity"
            )
            //busca steam en el navegador
            intent.putExtra(SearchManager.QUERY, "UCE")
            startActivity(intent)*/
        }

        val appResultLocal = registerForActivityResult(StartActivityForResult()) {
                resultActivity ->

            val sn = Snackbar.make(
                binding.editTextEmailAddress,
                "",
                Snackbar.LENGTH_LONG
            )

            //contrato con las clausulas a ejecutar
            var message = when(resultActivity.resultCode) {
                RESULT_OK -> {
                    sn.setBackgroundTint(resources.getColor(R.color.blue))
                    resultActivity.data?.getStringExtra("result")
                        .orEmpty() //si no hay nada, devuelve vacio
                }
                RESULT_CANCELED -> {
                    sn.setBackgroundTint(resources.getColor(R.color.red))
                    resultActivity.data?.getStringExtra("result")
                        .orEmpty()
                }
                else -> {
                    "Dudoso"
                }
            }
            sn.setText(message)
            sn.show()
        }

        val speechToText = registerForActivityResult(StartActivityForResult()){
                activityResult ->

            val sn = Snackbar.make(
                binding.editTextEmailAddress,
                "",
                Snackbar.LENGTH_LONG
            )

            var message = ""

            when(activityResult.resultCode){
                RESULT_OK -> {
                    val msg = activityResult.
                    data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )?.get(0).toString()

                    if(msg.isNotEmpty()){
                        val intent = Intent(
                            Intent.ACTION_WEB_SEARCH
                        )
                        intent.setClassName(
                            "com.google.android.googlequicksearchbox",
                            "com.google.android.googlequicksearchbox.SearchActivity"
                        )
                        Log.d("UCE",msg)
                        intent.putExtra(SearchManager.QUERY, msg)
                        startActivity(intent)
                    }
                }
                RESULT_CANCELED -> {
                    message = "Proceso cancelado"
                    sn.setBackgroundTint(resources.getColor(R.color.red))
                    sn.setText(message)
                    sn.show()
                }
                else -> {
                    message = "Ocurrio un error"
                    sn.setBackgroundTint(resources.getColor(R.color.red))
                    sn.setText(message)
                    sn.show()
                }
            }

        }

        binding.btnFacebook.setOnClickListener {

            val intentSpeech = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intentSpeech.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM //modelo de lenguaje libre
            )
            intentSpeech.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE, //en que idioma va a hblar
                Locale.getDefault() //toma el lenguaje del dispositivo
            )
            intentSpeech.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Di algo..."
            )
            speechToText.launch(intentSpeech)

            /*val resIntent = Intent(this, ResultActivity::class.java)
            appResultLocal.launch(resIntent)*/
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
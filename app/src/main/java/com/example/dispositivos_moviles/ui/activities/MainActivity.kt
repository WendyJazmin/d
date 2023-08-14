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
import android.location.Location
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import com.example.dispositivos_moviles.ui.utilities.MyLocationManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//sabado 15 de julio
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var client: SettingsClient
    private lateinit var locationSettingsRequest : LocationSettingsRequest

    //interfaz que nos va a permitir acceder a la ubicacion del ususario
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : LocationCallback

    private var currentLocation : Location? = null

    private lateinit var auth: FirebaseAuth
    private var TAG = "UCE"

    private val speechToText = registerForActivityResult(StartActivityForResult()){
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

    @SuppressLint("MissingPermission")
    private val locationContract = registerForActivityResult(RequestPermission()) {
            isGranted ->
        when(isGranted){
            true -> {
                client.checkLocationSettings(locationSettingsRequest).apply {
                    //si el GPs esta funcionando:
                    addOnSuccessListener {
                        val task = fusedLocationProviderClient.lastLocation
                        task.addOnSuccessListener {
                                location ->

                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest, //tipo ubicacion, tiempo
                                locationCallback, //resultado
                                Looper.getMainLooper() //loop
                            )
                        }
                    }

                    //si el GPS falla
                    addOnFailureListener {
                            ex ->
                        //si es una excepcion que la API puede solucionar
                        if(ex is ResolvableApiException) {
                            //lanza alert dialog listo para habilitar el GPS
                            ex.startResolutionForResult(
                                this@MainActivity,
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED
                            )
                        }
                    }
                }

                  /*  val alert = AlertDialog.Builder(
                        this,
                        com.google.android.material.R.style.ThemeOverlay_MaterialAlertDialog_Material3_Title_Icon
                    )
                    alert.apply {
                        setTitle("Alerta")
                        setMessage("Existe un problema con el sistema de posicionamiento global en el sistema")
                        setPositiveButton("Ok") {dialog, id ->
                            dialog.dismiss()
                        }
                        setNegativeButton("Cancelar") {dialog, id ->
                            dialog.dismiss()
                        }
                        setCancelable(false) //no puede tocar fuera el dialog hasta que toque alguna opcion
                    }.create()
                    alert.show()

                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest, //tipo ubicacion, tiempo
                        locationCallback, //resultado
                        Looper.getMainLooper() //loop
                    )
                }

                //cuando falla
                task.addOnFailureListener {ex->
                    if(ex is ResolvableApiException){
                        ex.startResolutionForResult(
                            this@MainActivity,
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED
                        )
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        auth = Firebase.auth

        binding.buttonIngresar.setOnClickListener {

            authWithFirebaseEmail(
                binding.editTextEmailAddress.text.toString(),
                binding.editTextTextPassword.text.toString()
            )
            /*
            signInWithEmailAndPassword(
                binding.editTextEmailAddress.text.toString(),
                binding.editTextTextPassword.text.toString()
            )*/

        }
        //


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        )
            //.setMaxUpdates(3) //cuantas veces se va a pedir la actu
            .build() //exactitud de ubicacion y tiempo en ms
        locationCallback = object : LocationCallback() {
            //Ctrl + O, override
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                if(locationResult != null){
                    locationResult.locations.forEach{
                            location ->
                        currentLocation = location
                        Log.d("UCE",
                            "Ubicacion: ${location.latitude}, " +
                                    "${location.longitude}")
                    }
                }
            }
        }

        client = LocationServices.getSettingsClient(this)
        locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()
    }

    //martes 8 de agosto
    // Guarda el usuario
    private fun authWithFirebaseEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Constants.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Authentication success.",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Constants.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    // Inicia sesion
    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    startActivity(Intent(this, SecondActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun recoveryPasswordWithEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                    task ->
                if(task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Correo de recuperacion enviado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    MaterialAlertDialogBuilder(this).apply {
                        setTitle("Alerta")
                        setMessage("Correo de recuperacion enviado correctamente")
                        setCancelable(true)
                    }.show()
                }
            }
    }
    //


    override fun onStart(){
        super.onStart()
        //initClass()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        //Para detener la obtencion de ubicacion al cambiar de activity
        fusedLocationProviderClient.removeLocationUpdates(
            locationCallback)
    }


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
                var snackbar = Snackbar.make(binding.textRegistrese ,"usuario o contrasenia invalidos",
                    Snackbar.LENGTH_LONG)//.show()
                snackbar.setBackgroundTint(getResources().getColor(R.color.black))
                snackbar.show()

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

    private suspend fun saveDataStore(stringData:String){
        dataStore.edit {prefs->//hace una funcion suspendida y se tiene que ejecutar en una corrutina
            prefs[stringPreferencesKey("usuario")]= stringData
            prefs[stringPreferencesKey("session")]= UUID.randomUUID().toString() //UUI universal User Identifier
            prefs[stringPreferencesKey("email")]= "dispomoviles@uce.edu.ec"
        }
    }

    //Inyeccion de dependencias
    private fun test(){
        var location = MyLocationManager(this)
        location.getUserLocation()
    }
}
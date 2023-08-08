package com.example.dispositivos_moviles.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dispositivos_moviles.databinding.ActivityBiometricBinding

import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.dispositivos_moviles.ui.viewmodels.BiometricViewModel
import kotlinx.coroutines.launch

class BiometricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricBinding

    //Implementacion del view model
    private val biometricViewModel by viewModels<BiometricViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAutentication.setOnClickListener {
            autenticationBiometric()
            lifecycleScope.launch {
                biometricViewModel.chargingData()
            }
        }
        biometricViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.lytMain.visibility = View.GONE
                binding.lytMainCopia.visibility = View.VISIBLE

            } else {
                binding.lytMain.visibility = View.VISIBLE
                binding.lytMainCopia.visibility = View.GONE
            }
        }
        lifecycleScope.launch {
            biometricViewModel.chargingData()
        }

    }


    private fun autenticationBiometric() {
        if (checkBiometric()) {
            val executor = ContextCompat.getMainExecutor(this)
            val biometricPrompt = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticacion requerida")
                .setSubtitle("Ingrese su huella digital")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()
            val biometricManager = BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        startActivity(Intent(this@BiometricActivity, SecondActivity::class.java))
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }
                })
            biometricManager.authenticate(biometricPrompt)
        } else {

            Snackbar.make(
                binding.textView2,
                "No existen los requisitos necesarios",
                Snackbar.LENGTH_LONG
            )
        }
    }

    private fun checkBiometric(): Boolean {
        var returnValid: Boolean = false
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                returnValid = true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                returnValid = false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                returnValid = false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val intentPrompt = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                intentPrompt.putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
                startActivity(intentPrompt)
                returnValid = false
            }
        }
        return returnValid
    }

}
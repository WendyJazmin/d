package com.example.dispositivos_moviles.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.databinding.ActivityBiometricBinding

import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


class BiometricActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBiometricBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAutentication.setOnClickListener{
            autenticateBiometric()
        }
    }

    private fun autenticateBiometric() {

        if(checkBiometric()) {
            val executor = ContextCompat.getMainExecutor(this)

            val biometricPrompt = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación requerida")
                .setSubtitle("Ingrese su huella digital")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .setNegativeButtonText("Cancelar")
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
                        startActivity(
                            Intent(
                            this@BiometricActivity,
                            CameraActivity::class.java)
                        )
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }
                })
            biometricManager.authenticate(biometricPrompt)
        } else {
            Snackbar.make(
                binding.btnAutentication,
                "No existen los requisitos necesarios",
                Snackbar.LENGTH_LONG).show()
        }
    }

    private fun checkBiometric() : Boolean {
        var returnValid : Boolean = false
        val biometricManager = BiometricManager.from(this)

        when(biometricManager.canAuthenticate(
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        )) {
            //hay hw y huella
            BiometricManager.BIOMETRIC_SUCCESS -> {
                returnValid = true
            }
            // no hay hw
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                returnValid = false
            }
            // hay problema con el hw
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                returnValid = false
            }
            // hay hw pero no huella
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val intentPrompt = Intent(Settings.ACTION_BIOMETRIC_ENROLL) // intent implicito: solicita a alguien que dé haciendo algo, no tiene punto de llegada, solo de salida
                intentPrompt.putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
                startActivity(intentPrompt)
                returnValid = true
            }
        }
        return returnValid
    }

}
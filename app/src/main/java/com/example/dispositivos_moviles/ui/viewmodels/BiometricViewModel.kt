package com.example.dispositivos_moviles.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class BiometricViewModel  : ViewModel() {

    var isLoading = MutableLiveData<Boolean>()

    // suspend porque el delay es una corrutina
    suspend fun chargingData() {
        isLoading.postValue(true)
        delay(5000) //va a simular carga de DB, lectura, procesamiento, etc
        isLoading.postValue(false)
    }
}
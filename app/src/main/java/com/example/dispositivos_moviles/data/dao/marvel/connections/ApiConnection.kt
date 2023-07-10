package com.example.dispositivos_moviles.data.dao.marvel.connections

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConnection {
/*
    fun getJikanConnection(): Retrofit {
        var retrofit = Retrofit.Builder()
            .baseUrl("https://api.jinkan.moe/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }*/

enum class typeApi {
    Jikan, Marvel
}

    private val API_JIKAN = "https://api.jikan.moe/v4/"
    private val API_MARVEL = "https://gateway.marvel.com/v1/public/"

    private fun getConnection(base: String): Retrofit? {
        var retrofit: Retrofit? = Retrofit.Builder()
            .baseUrl(base)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    //funcion suspendida
    suspend fun <T, E: Enum<E>> getService(api: E, service: Class<T>) : T {
        var base = ""
        when(api.name){
            typeApi.Jikan.name -> {
                base = API_JIKAN
            }

            typeApi.Marvel.name -> {
                base = API_MARVEL
            }
        }
        return getConnection(base)!!.create(service)
    }
}
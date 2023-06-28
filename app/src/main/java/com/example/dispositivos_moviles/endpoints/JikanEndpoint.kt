package com.example.dispositivos_moviles.endpoints

import com.example.dispositivos_moviles.data.entities.jikan.JikanAnimeEntity
import retrofit2.Response
import retrofit2.http.GET

interface JikanEndpoint {
    @GET("top/anime")
    suspend fun getAllAnimes() : Response<JikanAnimeEntity>
}
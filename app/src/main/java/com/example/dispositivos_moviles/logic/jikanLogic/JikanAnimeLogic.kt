package com.example.dispositivos_moviles.logic.jikanLogic

import com.example.dispositivos_moviles.data.dao.marvel.connections.ApiConnection
import com.example.dispositivos_moviles.data.endpoints.JikanEndpoint
import com.example.dispositivos_moviles.marvel.MarvelChars

class JikanAnimeLogic {
    suspend fun getAllAnimes() : List<MarvelChars> {

       // var call = ApiConnection.getJikanConnection()
        //val response = call.create(JikanEndpoint::class.java).getAllAnimes()

        var itemList = arrayListOf<MarvelChars>()
        val response = ApiConnection.getService(
            ApiConnection.typeApi.Jikan,
            JikanEndpoint::class.java
        ).getAllAnimes()

        if(response !=null) {
            if (response.isSuccessful) {
                if (response != null) {
                    response.body()!!.data.forEach {
                        val m = MarvelChars(
                            it.mal_id,
                            it.title,
                            it.titles[0].title,
                            it.images.jpg.image_url
                        )
                        itemList.add(m)
                    }
                }
            }
        }
        return itemList
    }
}
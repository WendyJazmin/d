package com.example.dispositivos_moviles.logic.marvelLogic

import android.util.Log
import com.example.dispositivos_moviles.data.dao.marvel.connections.ApiConnection
import com.example.dispositivos_moviles.endpoints.MarvelEndpoint
import com.example.dispositivos_moviles.logic.jikanLogic.characters.getMarvelChars
import com.example.dispositivos_moviles.marvel.MarvelChars


class MarvelLogic {
    private val key = "f00af94ad24dd1d56b2ea26ae903030e"

    suspend fun getMarvelChars(name : String, limit : Int): ArrayList<MarvelChars> {

        val itemList = arrayListOf<MarvelChars>()
        val response = ApiConnection.getService(
            ApiConnection.typeApi.Marvel,
            MarvelEndpoint::class.java
        ).getCharactersStartWith(name, limit)

        if(response.isSuccessful){
            response.body()!!.data.results.forEach {
                var comic : String = "Not available"
                if (it.comics.items.size > 0) {
                    comic = it.comics.items[0].name
                }
                val m = MarvelChars(
                    it.id,
                    it.name,
                    comic,
                    it.thumbnail.path + "." + it.thumbnail.extension
                )
                itemList.add(m)
            }
        } else {
            Log.d("UCE", response.toString())
        }
        return itemList
    }

    suspend fun getAllMarvelChars(offset : Int, limit : Int): ArrayList<MarvelChars> {

        val itemList = arrayListOf<MarvelChars>()

        val response = ApiConnection.getService(
            ApiConnection.typeApi.Marvel,
            MarvelEndpoint::class.java
        ).getAllMarvelChars(offset, limit)

        if(response != null){
            response.body()!!.data.results.forEach {
                val m = it.getMarvelChars()
                itemList.add(m)
            }
        }
        return itemList
    }
}
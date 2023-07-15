package com.example.dispositivos_moviles.logic.marvelLogic

import com.example.dispositivos_moviles.data.connections.ApiConnection
import com.example.dispositivos_moviles.data.endpoints.MarvelEndpoint


import com.example.dispositivos_moviles.data.entities.marvel.characters.dataBase.MarvelCharsDB
import com.example.dispositivos_moviles.data.entities.marvel.characters.dataBase.getMarvelChars
import com.example.dispositivos_moviles.data.entities.marvel.characters.getMarvelChars
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.example.dispositivos_moviles.logic.data.getMarvelCharsDB
import com.example.dispositivos_moviles.ui.utilities.Dispositivos_Moviles
import java.lang.Exception
import java.lang.RuntimeException


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
                val m = it.getMarvelChars()
                itemList.add(m)
            }
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

    suspend fun getAllMarvelCharsDB(): List<MarvelChars> {
        var itemList : ArrayList<MarvelChars> = arrayListOf()
        val items_aux = Dispositivos_Moviles.getDBInstance().marvelDao().getAllCharacters()
        items_aux.forEach {
            itemList.add( it.getMarvelChars())
        }
        return itemList
    }

    suspend fun insertMarvelCharsToDB(items : List<MarvelChars>) {
        var itemsDB = arrayListOf<MarvelCharsDB>()
        items.forEach {
            itemsDB.add(it.getMarvelCharsDB())
        }

        Dispositivos_Moviles
            .getDBInstance()
            .marvelDao()
            .insertMarvelChar(itemsDB)
    }

    //Martes 11 de julio
    suspend fun getInitChars(limit: Int, offset:Int): MutableList<MarvelChars> {
        var items = mutableListOf<MarvelChars>()
        try {
            var items = MarvelLogic()
                .getAllMarvelCharsDB()
                .toMutableList()

            if(items.isEmpty()){
                items = (MarvelLogic().getAllMarvelChars(
                    offset = offset, limit =limit
                ))
                MarvelLogic().insertMarvelCharsToDB(items)
            }
             items
        }catch (ex: Exception){
            throw RuntimeException(ex.message)
        }finally {
            return items
        }

    }
}
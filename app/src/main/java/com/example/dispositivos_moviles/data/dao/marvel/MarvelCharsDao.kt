package com.example.dispositivos_moviles.data.dao.marvel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dispositivos_moviles.entities.Marvel.dataBase.MarvelCharsDB

@Dao
interface MarvelCharsDao {

    @Query("select * from MarvelCharsDB")
    fun getAllCharacters() : List<MarvelCharsDao>

    @Query("select * from MarvelCharsDB where id = :pk")
    fun getOneCharacter(pk: Int): MarvelCharsDB

    @Insert
    fun insertMarvelChar(ch: List<MarvelCharsDB>)
}
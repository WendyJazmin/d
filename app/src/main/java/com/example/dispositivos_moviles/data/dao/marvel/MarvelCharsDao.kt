package com.example.dispositivos_moviles.data.dao.marvel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.dispositivos_moviles.data.entities.marvel.characters.dataBase.MarvelCharsDB

@Dao
interface MarvelCharsDao {

    @Query("select * from MarvelCharsDB")
    fun getAllCharacters() : List<MarvelCharsDB>

    @Query("select * from MarvelCharsDB where id = :pk")
    fun getOneCharacter(pk: Int): MarvelCharsDB

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMarvelChar(ch: List<MarvelCharsDB>)
}
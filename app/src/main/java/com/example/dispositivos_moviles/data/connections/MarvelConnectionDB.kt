package com.example.dispositivos_moviles.data.connections

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dispositivos_moviles.data.dao.marvel.MarvelCharsDao
import com.example.dispositivos_moviles.data.entities.marvel.characters.dataBase.MarvelCharsDB


@Database(
    entities =[MarvelCharsDB::class],
    version = 1)

    abstract class MarvelConnectionDB : RoomDatabase(){
        abstract fun marvelDao(): MarvelCharsDao
}


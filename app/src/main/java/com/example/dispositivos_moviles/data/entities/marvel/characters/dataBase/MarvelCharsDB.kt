package com.example.dispositivos_moviles.data.entities.marvel.characters.dataBase

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dispositivos_moviles.logic.data.MarvelChars

import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class MarvelCharsDB (

    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val name: String,
    val comic: String,
    val description: String,
    val image: String
        ): Parcelable

fun MarvelCharsDB.getMarvelChars() : MarvelChars {
    return  MarvelChars(
        id,
        name,
        comic,
        description,
        image
    )
}

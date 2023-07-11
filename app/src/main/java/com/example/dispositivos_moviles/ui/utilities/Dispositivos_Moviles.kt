package com.example.dispositivos_moviles.ui.utilities

import android.app.Application
import androidx.room.Room
import com.example.dispositivos_moviles.data.connections.MarvelConnectionDB

class Dispositivos_Moviles : Application() {

    val name_class : String = "Admin"

    override fun onCreate(){
        super.onCreate()
        db = Room.databaseBuilder(applicationContext,
            MarvelConnectionDB::class.java,
            "marvelDB").build()
    }

    companion object{//es un objeto que se crea dentro de una clase
        private var db : MarvelConnectionDB? = null

        fun getDBInstance() : MarvelConnectionDB {
            //!! -> porque nunca va a ser nula
            return db!!
        }
    }
}
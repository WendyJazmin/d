package com.example.dispositivos_moviles.logic.validator

import com.example.dispositivos_moviles.entities.LoginUser

class LoginValidator {

    fun checklogin(name:String, password:String):Boolean{
        val admin = LoginUser()
        return (admin.name == name && admin.pass == password)
    }
}
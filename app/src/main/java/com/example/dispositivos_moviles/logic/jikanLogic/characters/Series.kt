package com.example.dispositivos_moviles.logic.jikanLogic.characters

data class Series(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
)
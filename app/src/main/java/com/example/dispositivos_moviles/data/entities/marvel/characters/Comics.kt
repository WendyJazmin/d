package com.example.dispositivos_moviles.data.entities.marvel.characters

data class Comics(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
)
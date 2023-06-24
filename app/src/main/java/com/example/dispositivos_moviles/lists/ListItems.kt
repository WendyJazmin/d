package com.example.dispositivos_moviles.lists

import com.example.dispositivos_moviles.data.entities.LoginUser
import com.example.dispositivos_moviles.marvel.MarvelChars

class ListItems {

    fun returnItems(): List<LoginUser> {
        var items = listOf<LoginUser>(
            LoginUser("1", "1"),
            LoginUser("2", "2"),
            LoginUser("3", "3"),
            LoginUser("4", "4"),
            LoginUser("5", "5")
        )
        return items
    }

    fun returnMarvelChars(): List<MarvelChars> {
        val items = listOf(
            MarvelChars(
                1,
                "Daredevil",
                "Daredevil Issue #512 (1998)",
                "https://comicvine.gamespot.com/a/uploads/original/11174/111743204/8923730-vincenzoriccardi.jpg"
            ),
            MarvelChars(
                2,
                "Punisher",
                "The Punisher Issue #104 (1995)",
                "https://comicvine.gamespot.com/a/uploads/original/11/115213/8075122-punisher-marvel-comics.jpg"
            ),
            MarvelChars(
                3,
                "Spider-Man 2099",
                "Spider-Man 2099 Issue #46 (1996)",
                "https://comicvine.gamespot.com/a/uploads/original/11170/111705043/8844222-2099.jpg"
            ),
            MarvelChars(
                4,
                "Spider-Man",
                "The Amazing Spider-Man Issue #700 (2013)",
                "https://comicvine.gamespot.com/a/uploads/original/11170/111705043/8843794-neverready.jpg"
            ),
            MarvelChars(
                5,
                "Magik",
                "The New Mutants Issue #100 (1991)",
                "https://comicvine.gamespot.com/a/uploads/original/12/124259/8939731-large-5909745.jpg"
            )
        )
        return items
    }
}
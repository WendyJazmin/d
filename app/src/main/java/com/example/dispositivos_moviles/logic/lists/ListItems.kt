package com.example.dispositivos_moviles.logic.lists


import com.example.dispositivos_moviles.logic.data.MarvelChars

class ListItems {

    fun returnItems(): List<com.example.dispositivos_moviles.data.entities.LoginUser> {
        var items = listOf<com.example.dispositivos_moviles.data.entities.LoginUser>(
            com.example.dispositivos_moviles.data.entities.LoginUser("1", "1"),
            com.example.dispositivos_moviles.data.entities.LoginUser("2", "2"),
            com.example.dispositivos_moviles.data.entities.LoginUser("3", "3"),
            com.example.dispositivos_moviles.data.entities.LoginUser("4", "4"),
            com.example.dispositivos_moviles.data.entities.LoginUser("5", "5")
        )
        return items
    }

    fun returnMarvelChars(): List<MarvelChars> {
        val items = listOf(
            MarvelChars(
                1,
                "Daredevil",
                "Daredevil Issue #512 (1998)",
                "",
                "https://comicvine.gamespot.com/a/uploads/original/11174/111743204/8923730-vincenzoriccardi.jpg"
            ),
            MarvelChars(
                2,
                "Punisher",
                "The Punisher Issue #104 (1995)",
                "",
                "https://comicvine.gamespot.com/a/uploads/original/11/115213/8075122-punisher-marvel-comics.jpg"
            ),
            MarvelChars(
                3,
                "Black Cat",
                "The Astonishing Spider-Man Issue #62 (2009)",
                "",
                "https://comicvine.gamespot.com/a/uploads/original/12/124259/8106984-black_cat_vol_2_9_sway_variant_textless.jpg"
            ),
            MarvelChars(
                4,
                "Spider-Man",
                "The Amazing Spider-Man Issue #700 (2013)",
                "",
                "https://comicvine.gamespot.com/a/uploads/original/13/132327/6605716-spider_man_ps4_by_patrickbrown-dcm8o9q.jpg"

            ),
            MarvelChars(
                5,
                "Scarlet Witch",
                "House of M Issue #4 (2006)",
                "",
                "https://comicvine.gamespot.com/a/uploads/original/12/124259/7930568-women_of_marvel_vol_2_1_lee_exclusive_virgin_variant.jpg"
            )
        )
        return items
    }
}
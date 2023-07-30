package com.example.dispositivos_moviles.ui.activities



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.databinding.ActivityDetailsMarvelItemBinding
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.squareup.picasso.Picasso
import pl.droidsonroids.gif.GifImageView


class DetailsMarvelItem : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsMarvelItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsMarvelItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val imgBackground = findViewById<GifImageView>(R.id.imgBackground)
    }

    override fun onStart() {
        super.onStart()
        //? significa que la var puede ser null
        /*var name: String? = ""
        intent.extras?.let {
            name = it.getString("name")

        }
        if(!name.isNullOrEmpty()){
            binding.txtName.text = name
        }*/

        //con <> le pido que me regrese a la forma anterior de MarvelChars
        val item = intent.getParcelableExtra<MarvelChars>("name")
//aparece los detalles de cada item
        if(item != null) {
            binding.txtName.text = item.name
            Picasso.get().load(item.image).into(binding.imgMarvel)
           binding.txtComic.text = item.comic
            binding.txtDescription.text = item.description
        }
    }
}
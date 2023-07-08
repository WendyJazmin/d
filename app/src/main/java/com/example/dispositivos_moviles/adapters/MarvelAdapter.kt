package com.example.dispositivos_moviles.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.databinding.MarvelCharactersBinding
import com.example.dispositivos_moviles.marvel.MarvelChars
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class MarvelAdapter(

    //Unit es igual al void en java, no devuelve nada
                    private val fnClick: (MarvelChars) -> Unit):
    RecyclerView.Adapter<MarvelAdapter.MarvelViewHolder>() {
    var items: List<MarvelChars> = listOf()

    class MarvelViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding: MarvelCharactersBinding =
            MarvelCharactersBinding.bind(view)

        fun render(item: MarvelChars,
                   fnClick: (MarvelChars) -> Unit){
            binding.imgMarvel.bringToFront()
            binding.txtName.text = item.name
            binding.txtComic.text = item.comic
            Picasso.get().load(item.image).into(binding.imgMarvel)

            itemView.setOnClickListener{
                fnClick(item)
                //Snackbar.make(binding.imgMarvel, item.name, Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarvelAdapter.MarvelViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return MarvelViewHolder(
            inflater.inflate(
                R.layout.marvel_characters2, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MarvelAdapter.MarvelViewHolder, position: Int) {
        holder.render(items[position], fnClick)
    }

    override fun getItemCount(): Int = items.size

    fun updateListItems(newItems: List<MarvelChars>){
        //plus agrega a la lista los nuevos elems
        items = items.plus(newItems)
        notifyDataSetChanged()
    }

    fun replaceListItems(newItems: List<MarvelChars>){
        //plus agrega a la lista los nuevos elems
        this.items = newItems
        notifyDataSetChanged()
    }

}
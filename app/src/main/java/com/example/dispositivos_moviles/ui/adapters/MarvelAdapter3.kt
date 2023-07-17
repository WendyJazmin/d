package com.example.dispositivos_moviles.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.databinding.MarvelCharactersBinding
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.squareup.picasso.Picasso

class MarvelAdapter3 (
    private var items: List<MarvelChars>,
    private var fnClick: (MarvelChars) -> Unit //Este unit no te devuelve nada.
) : //adaptor recibe dos parametros.
    RecyclerView.Adapter<MarvelAdapter3.MarvelViewHolder>() {


    class MarvelViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding: MarvelCharactersBinding =
        MarvelCharactersBinding.bind(view) // binding de layout creado

        fun render(
            item: MarvelChars,
            fnClick: (MarvelChars) -> Unit
        ) { //tomar cada uno de los items de la lista
            //println("Recibiendo a ${item.nombre}")
            binding.txtName.text = item.name
            binding.txtComic.text = item.comic

            //picasso
            Picasso.get().load(item.image).into(binding.imgMarvel)

            itemView.setOnClickListener {
                fnClick(item)
                //Snackbar.make(binding.imgMarvel, item.name, Snackbar.LENGTH_LONG).setBackgroundTint(Color.rgb(247, 147, 76)).show()
            }
        }
    }

    // Creacion de layout xml (element)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarvelViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MarvelViewHolder(
            inflater.inflate(
                R.layout.marvel_characters3,
                parent,
                false
            )
        )
        // val a = inflater.inflate(R.layout.marvel_personajes, parent, false)
        // return MarvelViewHolder(a)
    }

    override fun onBindViewHolder(holder: MarvelViewHolder, position: Int) {
        holder.render(items[position], fnClick)
    }

    override fun getItemCount(): Int = items.size

    fun updateListItems(newItems: List<MarvelChars>) {
        this.items = this.items.plus(newItems)
        notifyDataSetChanged()
    }

    fun replaceListItems(newItems: List<MarvelChars>) {
        this.items = newItems
        notifyDataSetChanged()
    }

}
package com.example.dispositivos_moviles.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.activities.DetailsMarvelItem
import com.example.dispositivos_moviles.adapters.MarvelAdapter
import com.example.dispositivos_moviles.databinding.FragmentFirstBinding
import com.example.dispositivos_moviles.lists.ListItems
import com.example.dispositivos_moviles.logic.jikanLogic.JikanAnimeLogic
import com.example.dispositivos_moviles.logic.marvelLogic.MarvelLogic
import com.example.dispositivos_moviles.marvel.MarvelChars
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FirstFragment : Fragment() {

    private var page = 1
    private lateinit var binding: FragmentFirstBinding
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var gManager : GridLayoutManager

    private var rvAdapter: MarvelAdapter = MarvelAdapter { sendMarvelItem(it) }

    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf<MarvelChars>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
        lmanager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
       gManager = GridLayoutManager(requireActivity(),2)
        return binding.root
    }


    override fun onStart() {
        super.onStart()

        val names = arrayListOf<String>(
            "Carlos",
            "Xavier",
            "Andrés",
            "Pepe",
            "Mariano",
            "Rosa"
        )

        val adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.simple_layout,
            names
        )
        binding.spinner.adapter = adapter
        chargeDataRV("cap")

        binding.rvSwipe.setOnRefreshListener {
            chargeDataRV("cap")
            binding.rvSwipe.isRefreshing = false
        }

        binding.rvMarvelChars.addOnScrollListener(
            object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    //dy: posicion vertical
                    if(dy > 0){

                        //cuantos elems han pasado
                        val v = lmanager.childCount
                        //mi posicion actual
                        val p = lmanager.findFirstVisibleItemPosition()
                        //cuantos elems tengo en total
                        val t = lmanager.itemCount

                        //si la posicion actual mas los elems que han pasado, entonces tengo que recargar
                        if((v + p) >= t){
                            //en corutina IO
                            lifecycleScope.launch(Dispatchers.IO){
                                val newItems = JikanAnimeLogic().getAllAnimes()
                                /*val newItems = MarvelLogic().getMarvelChars(
                                    name = "spider",
                                    limit = 20
                                )*/
                                //cambio de corutina a Main
                                withContext(Dispatchers.Main){
                                    rvAdapter.updateListItems(newItems)
                                }
                            }
                        }

                    }
                }
            })

        //se importa el que tiene llaves y dice Editable
       binding.txtFilter.addTextChangedListener{ filterText ->
            val newItems = marvelCharsItems.filter {
                    items -> items.name.lowercase().contains(
                filterText.toString().lowercase())
            }
            rvAdapter.replaceListItems(newItems)
        }
    }

    fun sendMarvelItem(item: MarvelChars) {
        //Intents solo estan en fragments y activities
        val i = Intent(requireActivity(), DetailsMarvelItem::class.java)
        i.putExtra("name", item)
        startActivity(i)
    }

    // Serializacion: pasar de un objeto a un string para poder enviarlo por medio de la web, usa obj JSON
    // Parceables: Mucho mas eficiente que la serializacion pero su implementacion es compleja, pero existen plugins que nos ayudan
    fun chargeDataRV(search: String) {

        lifecycleScope.launch(Dispatchers.Main) {
            marvelCharsItems.addAll(withContext(Dispatchers.IO) {
                return@withContext (MarvelLogic().getMarvelChars(
                    name = search, 20
                ))
            })

            //rvAdapter = MarvelAdapter(marvelCharsItems, fnClick = { sendMarvelItem(it) })
            rvAdapter.items = marvelCharsItems
            binding.rvMarvelChars.apply {
                this.adapter = rvAdapter
                this.layoutManager = layoutManager
            }
            //  lmanager.scrollToPositionWithOffset(pos, 10)
        }

    }
}




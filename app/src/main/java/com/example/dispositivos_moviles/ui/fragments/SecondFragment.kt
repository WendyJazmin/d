package com.example.dispositivos_moviles.ui.fragments

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
import com.example.dispositivos_moviles.databinding.FragmentFirstBinding
import com.example.dispositivos_moviles.ui.activities.DetailsMarvelItem
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter2
import com.example.dispositivos_moviles.databinding.FragmentSecondBinding
import com.example.dispositivos_moviles.logic.lists.ListItems
import com.example.dispositivos_moviles.logic.jikanLogic.JikanAnimeLogic
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.example.dispositivos_moviles.logic.marvelLogic.MarvelLogic
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter
import com.example.dispositivos_moviles.ui.utilities.Metodos
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SecondFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var gManager: GridLayoutManager//para hacer en 2 columnas

    private var rvAdapter : MarvelAdapter2 = MarvelAdapter2 { sendMarvelItem(it) }
    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf<MarvelChars>()

    //martes 11 de julio
    private val limit = 99
    private var offset = 6
    private var page = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_first, container, false)
        lmanager = LinearLayoutManager(
            requireActivity(), LinearLayoutManager.VERTICAL,
            false
        )
        gManager = GridLayoutManager(requireActivity(), 2)
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
            "Rosa")

        val adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.simple_layout,
            names
        )


        // chargeDataRV("cap")
        //chargeDataRVDB(5)
        chargeDataRVAPI(offset = offset, limit = limit)

        //cargando
        binding.rvSwipe2.setOnRefreshListener {
            //chargeDataRVDB(5)
            chargeDataRVAPI(offset = offset, limit = limit)
            binding.rvSwipe2.isRefreshing = false
            lmanager.scrollToPositionWithOffset(5, 30)
        }

        binding.rvMarvelChars2.addOnScrollListener(
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
                                //val newItems = JikanAnimeLogic().getAllAnimes()
                                val newItems = MarvelLogic().getAllMarvelChars(0, 99)
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
        binding.txtFilter2.addTextChangedListener { filteredText ->
            val newItems = marvelCharsItems.filter { items ->
                items.name.lowercase().contains(filteredText.toString().lowercase())
            }
            rvAdapter.replaceListItems(newItems)
        }


    }

    //Un intent se encuentra en un activity o un fragment
    //una analogia a serializacion es el parcelable, es  mas eficiente pero mas dificil de implementar
    fun sendMarvelItem(item: MarvelChars) {
        val i = Intent(requireActivity(), DetailsMarvelItem::class.java)
        i.putExtra("name", item)
        startActivity(i)
    }


    fun chargeDataRV(search: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            rvAdapter.items = MarvelLogic().getAllMarvelChars(0, 99)

            withContext(Dispatchers.Main) {
                with(binding.rvMarvelChars2) {
                    this.adapter = rvAdapter
                    //this.layoutManager = gManager
                }
            }
        }
    }





    fun chargeDataRVDB(pos: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            marvelCharsItems.addAll(withContext(Dispatchers.IO) {
                return@withContext MarvelLogic().getAllMarvelCharsDB().toMutableList()
            })

            if (marvelCharsItems.isEmpty()) {
                marvelCharsItems = withContext(Dispatchers.IO) {
                    return@withContext (MarvelLogic().getAllMarvelChars(
                        0,
                        page * 3
                    )).toMutableList()
                }
            }

            withContext(Dispatchers.IO) {
                MarvelLogic().insertMarvelCharsToDB(marvelCharsItems)
            }

            rvAdapter.items = marvelCharsItems
            binding.rvMarvelChars2.apply {
                this.adapter = rvAdapter
                this.layoutManager = gManager
                gManager.scrollToPositionWithOffset(pos, 10)
            }
        }
        page++
    }


    //martes 11 de julio
    fun chargeDataRVAPI(limit: Int, offset: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            // marvelCharsItems.addAll(withContext(Dispatchers.IO) {
            marvelCharsItems = withContext(Dispatchers.IO) {
                return@withContext (MarvelLogic().getAllMarvelChars(
                    offset, limit
                ))
            }
            rvAdapter.items = marvelCharsItems
            binding.rvMarvelChars2.apply {
                this.adapter = rvAdapter
                this.layoutManager = gManager
            }
            this@SecondFragment.offset = offset + limit
        }
    }


    fun chargeDataRVInit(limit: Int, offset: Int) {

        if(Metodos().isOnline(requireActivity())){
            lifecycleScope.launch(Dispatchers.Main) {
                marvelCharsItems = withContext(Dispatchers.IO) {
                    return@withContext MarvelLogic().getInitChars(limit,offset)

                }
                rvAdapter.items = marvelCharsItems
                binding.rvMarvelChars2.apply {
                    this.adapter = rvAdapter
                    this.layoutManager = gManager
                }
                this@SecondFragment.offset += limit
            }
        }else {
            Snackbar.make(
                binding.cardView,
                "No hay conexion",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }


}

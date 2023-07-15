package com.example.dispositivos_moviles.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
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
import com.example.dispositivos_moviles.ui.activities.DetailsMarvelItem
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter
import com.example.dispositivos_moviles.databinding.FragmentFirstBinding
import com.example.dispositivos_moviles.logic.marvelLogic.MarvelLogic
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.example.dispositivos_moviles.logic.data.getMarvelCharsDB
import com.example.dispositivos_moviles.ui.utilities.Dispositivos_Moviles
import com.example.dispositivos_moviles.ui.utilities.Metodos
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FirstFragment : Fragment() {

    private var page = 1
    private lateinit var binding: FragmentFirstBinding
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var gManager: GridLayoutManager //para hacer en dos columnas

    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf<MarvelChars>()
    private var rvAdapter: MarvelAdapter = MarvelAdapter {sendMarvelItem(it)}//,{saveMarvelItem(it)})

    //martes 11 de julio
    private val limit = 99
    private var offset = 6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
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
            "Rosa"
        )

        val adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.simple_layout,
            names
        )

        binding.spinner.adapter = adapter
        // chargeDataRV("cap")
        chargeDataRVDB(5)


//        binding.rvSwipe.setOnRefreshListener {
//            chargeDataRV(5)
//            binding.rvSwipe.isRefreshing = false
//        }

        //cargando
        binding.rvSwipe.setOnRefreshListener {
            chargeDataRVAPI(offset = offset, limit = limit)
            binding.rvSwipe.isRefreshing = false
            lmanager.scrollToPositionWithOffset(5, 20)
        }

        binding.rvMarvelChars.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)//dy para abajo contando y mostrando, y la dx de izquierda a derecha

                    if (dy > 0) {
                        val v = lmanager.childCount//cuantos elementos tengo
                        val p = lmanager.findFirstVisibleItemPosition()//cual es mi posicion actual
                        val t = lmanager.itemCount//cuantos tengo en toal

                        if ((v + p) >= t) {
                            lifecycleScope.launch((Dispatchers.Main)) {
                                /*  val newItems = JikanAnimeLogic().getAllAnimes()*/
                                val items = with(Dispatchers.IO){
                                    MarvelLogic().getAllMarvelChars(offset,limit)
                                }
                                rvAdapter.updateListItems((items))
                                this@FirstFragment.offset += offset
                            }
                        }
                    }
                }
            })

        binding.txtFilter.addTextChangedListener { filteredText ->
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

    fun saveMarvelItem(item : MarvelChars ) : Boolean {
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Dispositivos_Moviles
                    .getDBInstance()
                    .marvelDao()
                    .insertMarvelChar(listOf(item.getMarvelCharsDB()))

            }
        }
        return true
    }



    /*
        fun corrtine(){
            lifecycleScope.launch(Dispatchers.Main){
                var name="dave"
              name= withContext(Dispatchers.IO){
                    name = "Maria"
                  return@withContext name
                }
                //aqui va el codigo que necesitemos
               // binding.card1Fragment.radius
            }
        }
        */
    fun chargeDataRV(search: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            rvAdapter.items = MarvelLogic().getAllMarvelChars(0, 99)

            withContext(Dispatchers.Main) {
                with(binding.rvMarvelChars) {
                    this.adapter = rvAdapter
                    this.layoutManager = gManager
                }
            }
        }

        /* lifecycleScope.launch(Dispatchers.Main) {
            marvelCharsItems.addAll(withContext(Dispatchers.IO) {
                 return@withContext (MarvelLogic().getMarvelChars(
                     "spider", 20
                 ))
             })

             //rvAdapter = MarvelAdapter(marvelCharsItems, fnClick = { sendMarvelItem(it) })
             rvAdapter.items = marvelCharsItems
             binding.rvMarvelChars.apply {
                 this.adapter = rvAdapter
                 this.layoutManager = layoutManager
             }
           //  lmanager.scrollToPositionWithOffset(pos, 10)
         }*/

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
            binding.rvMarvelChars.apply {
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
            binding.rvMarvelChars.apply {
                this.adapter = rvAdapter
                this.layoutManager = gManager
            }
            this@FirstFragment.offset = offset + limit
        }
    }


    fun chargeDataRVInit(limit: Int, offset: Int) {

        if(Metodos().isOnline(requireActivity())){
            lifecycleScope.launch(Dispatchers.Main) {
                marvelCharsItems = withContext(Dispatchers.IO) {
                    return@withContext MarvelLogic().getInitChars(limit,offset)

                }
                rvAdapter.items = marvelCharsItems
                binding.rvMarvelChars.apply {
                    this.adapter = rvAdapter
                    this.layoutManager = gManager
                }
                this@FirstFragment.offset += limit
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


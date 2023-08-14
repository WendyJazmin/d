package com.example.dispositivos_moviles.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.ui.activities.DetailsMarvelItem
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter2
import com.example.dispositivos_moviles.databinding.FragmentSecondBinding
import com.example.dispositivos_moviles.databinding.FragmentThirdBinding
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.example.dispositivos_moviles.logic.data.getMarvelCharsDB
import com.example.dispositivos_moviles.logic.marvelLogic.MarvelLogic
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter3
import com.example.dispositivos_moviles.ui.utilities.Dispositivos_Moviles
import com.example.dispositivos_moviles.ui.utilities.Metodos
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SecondFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding
    private lateinit var lManager: LinearLayoutManager
    private lateinit var gManager: GridLayoutManager

    private var rvAdapter: MarvelAdapter2 = MarvelAdapter2 { sendMarvelItem(it) }
    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf<MarvelChars>()

    //martes 11 de julio
    private val limit = 99
    private var offset = 6
    private var page = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSecondBinding.inflate(layoutInflater, container, false)
        lManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
         gManager = GridLayoutManager(requireActivity(), 2)
        return binding.root
    }

    override fun onStart() {
        super.onStart();

       // chargeDataRVInit(limit, offset)//martes 11 de julio, comprueba si existe conexion
        chargeDataRVAPI(offset = offset, limit = limit)

        //CARGANDO
        binding.rvSwipe2.setOnRefreshListener {
            chargeDataRVInit(limit, offset)//martes 11 de julio, comprueba si existe conexion
            //chargeDataRVAPI(offset = offset, limit = limit)
            binding.rvSwipe2.isRefreshing = false
            lManager.scrollToPositionWithOffset(5, 20)
        }

        binding.rvMarvelChars2.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val v = lManager.childCount //cuantos elems han pasado
                        val p = lManager.findFirstVisibleItemPosition() //mi posicion actual
                        val t = lManager.itemCount //cuantos elems tengo en total

                        if ((v + p) >= t) {
                            lifecycleScope.launch((Dispatchers.Main))
                            {
                                /*  val newItems = JikanAnimeLogic().getAllAnimes()*/
                                val items = with(Dispatchers.IO) {
                                    MarvelLogic().getAllMarvelChars(offset, limit)
                                }
                                rvAdapter.updateListItems(items)
                                this@SecondFragment.offset+=limit

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

    fun sendMarvelItem(item: MarvelChars) {//se obtiene la información(detalles) de cada item
        //Intent(contexto de la activity, .class de la activity)
        val i = Intent(requireActivity(), DetailsMarvelItem::class.java)
        i.putExtra("name", item)
        startActivity(i)
    }

    //Martes 11 de julio
    //guardar en favoritos
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


    fun chargeDataRV(search: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            rvAdapter.items = MarvelLogic().getAllMarvelChars(0, 99)

            withContext(Dispatchers.Main) {
                with(binding.rvMarvelChars2) {
                    this.adapter = rvAdapter
                    this.layoutManager = lManager
                    // this.layoutManager = gManager//para hacer 2 columnas

                }
            }
        }
    }

    //insertar en la base de datos
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
                this.layoutManager = lManager
               // this.layoutManager = gManager
               // gManager.scrollToPositionWithOffset(pos, 10)
            }
        }
        page++
    }

    //martes 11 de julio
    fun chargeDataRVAPI(limit: Int, offset: Int) {
        if (Metodos().isOnline(requireActivity())) {
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
                    //this.layoutManager = lManager
                    this.layoutManager = gManager//para hacer 2 columnas
                }
                this@SecondFragment.offset = offset + limit
            }
        } else {
            //Snackbar.make(binding.cardView, "No hay conexion", Snackbar.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), "No hay conexion", Toast.LENGTH_SHORT).show()

        }
    }
    fun chargeDataRVInit(limit: Int,offset: Int) {
        if (Metodos().isOnline(requireActivity())) {
            lifecycleScope.launch(Dispatchers.Main) {
                marvelCharsItems = withContext(Dispatchers.IO) {
                    return@withContext MarvelLogic().getInitChars(limit, offset)
                }

                this@SecondFragment.offset +=limit
                rvAdapter.items = marvelCharsItems
                binding.rvMarvelChars2.apply {
                    this.adapter = rvAdapter
                    //this.layoutManager = lManager
                     this.layoutManager = gManager//para hacer 2 columnas
                }
            }
        } else {
            //Snackbar.make(requireContext(), "No hay conexion", Snackbar.LENGTH_LONG).show()
            Toast.makeText(requireContext(), "No hay conexion", Toast.LENGTH_SHORT).show()
        }
    }
}
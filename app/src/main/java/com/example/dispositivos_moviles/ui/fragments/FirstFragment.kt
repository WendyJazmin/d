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
import android.widget.Toast
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

//import sabado 15 de julio
import com.example.dispositivos_moviles.ui.activities.dataStore
import com.example.dispositivos_moviles.ui.adapters.data.UserDataStore
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.stringPreferencesKey


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var gManager: GridLayoutManager  //para hacer en dos columnas

    private var rvAdapter: MarvelAdapter = MarvelAdapter { sendMarvelItem(it) }
    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf()

    //martes 11 de julio
    private var limit = 99
    private var offset = 0
    private var page = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
        lmanager = LinearLayoutManager(
            requireActivity(), LinearLayoutManager.VERTICAL,
            false
        )
        gManager = GridLayoutManager(requireActivity(), 2)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        //sabado 15 de julio
        lifecycleScope.launch(Dispatchers.Main) {
            getDataStore().collect { user ->
                Log.d("------------>> UCE email", user.email)
                Log.d("------------>> UCE name", user.name)
                Log.d("------------>> UCE session", user.session)
            }
        }
        //

        val names = arrayListOf<String>(
            "Carlos",
            "Xavier",
            "Andrés",
            "Pepe",
            "Mariano",
            "Rosa"
        )

        val adapter = ArrayAdapter(
            requireActivity(),
            //el simple spinner no es de nadie :v
            R.layout.simple_layout,
            names
        )

        binding.spinner.adapter = adapter
        //chargeDataRVInit(limit, offset)//martes 11 de julio, comprueba si existe conexion
        chargeDataRVAPI(offset = offset, limit = limit)

        //CARGANDO
        binding.rvSwipe.setOnRefreshListener {
            chargeDataRVInit(limit, offset)//martes 11 de julio, comprueba si existe conexion
           // chargeDataRVAPI(offset = offset, limit = limit)
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
                                val items = with(Dispatchers.IO) {
                                    MarvelLogic().getAllMarvelChars(offset, limit)
                                }

                                rvAdapter.updateListItems(items)
                                this@FirstFragment.offset+=limit
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

    //guardar en favoritos
    /* fun saveMarvelItem(item : MarvelChars ) : Boolean {
         lifecycleScope.launch(Dispatchers.Main) {
             withContext(Dispatchers.IO) {
                 Dispositivos_Moviles
                     .getDBInstance()
                     .marvelDao()
                     .insertMarvelChar(listOf(item.getMarvelCharsDB()))

             }
         }
         return true
     }*/

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

    //Martes 11 de julio
    fun chargeDataRVAPI(limit: Int, offset: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
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
            this@FirstFragment.offset += limit
        }
    }

    fun chargeDataRVInit(limit: Int,offset: Int) {
        if (Metodos().isOnline(requireActivity())) {
            lifecycleScope.launch(Dispatchers.Main) {
                marvelCharsItems = withContext(Dispatchers.IO) {
                    return@withContext MarvelLogic().getInitChars(limit, offset)
                }

                this@FirstFragment.offset +=limit
                rvAdapter.items = marvelCharsItems
                binding.rvMarvelChars.apply {
                    this.adapter = rvAdapter
                    this.layoutManager = gManager//para hacer 2 columnas
                }
            }
        } else {
            //Snackbar.make(requireContext(), "No hay conexion", Snackbar.LENGTH_LONG).show()
            Toast.makeText(requireContext(), "No hay conexion", Toast.LENGTH_SHORT).show()
        }
    }

    //sabado 15 de julio
    private fun getDataStore() =
        requireActivity().dataStore.data.map { prefs ->
            UserDataStore(
                name= prefs[stringPreferencesKey("usuario")].orEmpty(),
                email= prefs[stringPreferencesKey("contrasenia")].orEmpty(),
                session =  prefs[stringPreferencesKey("pass")].orEmpty()
            )
        }

}


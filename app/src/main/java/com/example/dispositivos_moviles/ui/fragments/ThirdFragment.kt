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
import com.example.dispositivos_moviles.databinding.FragmentThirdBinding
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.example.dispositivos_moviles.logic.jikanLogic.JikanAnimeLogic
import com.example.dispositivos_moviles.logic.marvelLogic.MarvelLogic
import com.example.dispositivos_moviles.ui.activities.DetailsMarvelItem
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter3
import com.example.dispositivos_moviles.ui.utilities.Metodos
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ThirdFragment : Fragment() {
    private lateinit var binding: FragmentThirdBinding
    private lateinit var lManager: LinearLayoutManager
    private lateinit var gManager: GridLayoutManager

    private var rvAdapter: MarvelAdapter3 = MarvelAdapter3 { sendMarvelItem(it) }
    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf<MarvelChars>()

    //martes 11 de julio
    private val limit = 99
    private var offset = 6
    private var page = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentThirdBinding.inflate(
            layoutInflater, container, false)

        lManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )

       // gManager = GridLayoutManager(requireActivity(), 2)
        return binding.root    }

    override fun onStart() {
        super.onStart();

        chargeDataRVInit(limit, offset)
       // chargeDataRVAPI(offset = offset, limit = limit)

        binding.rvSwipe3.setOnRefreshListener {
            chargeDataRVAPI(offset = offset, limit = limit)
            binding.rvSwipe3.isRefreshing = false
            lManager.scrollToPositionWithOffset(5, 20)
        }

        binding.rvMarvelChars3.addOnScrollListener(
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
                                val items = with(Dispatchers.IO) {
                                    MarvelLogic().getMarvelChars("spider", page * 3)
                                    //JikanAnimeLogic().getAllAnimes()
                                }
                                rvAdapter.updateListItems((items))

                            }
                        }

                    }

                }
            })

        binding.txtFilter3.addTextChangedListener { filteredText ->
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

    fun chargeDataRV(pos: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            //rvAdapter.items = JikanAnimeLogic().getAllAnimes()
            marvelCharsItems = withContext(Dispatchers.IO) {
                return@withContext (MarvelLogic().getMarvelChars(
                    "spider", page * 3
                ))
            }
            rvAdapter.items = marvelCharsItems

            binding.rvMarvelChars3.apply {
                this.adapter = rvAdapter;
                this.layoutManager = lManager;

                gManager.scrollToPositionWithOffset(pos, 10)
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
                binding.rvMarvelChars3.apply {
                    this.adapter = rvAdapter
                    this.layoutManager = lManager
                }
                this@ThirdFragment.offset = offset + limit
            }

            } else {
                //Snackbar.make(requireContext(), "No hay conexion", Snackbar.LENGTH_LONG).show()
                Toast.makeText(requireContext(), "No hay conexion", Toast.LENGTH_SHORT).show()
            }
    }
    fun chargeDataRVInit(limit: Int,offset: Int) {
        if (Metodos().isOnline(requireActivity())) {
            lifecycleScope.launch(Dispatchers.Main) {
                marvelCharsItems = withContext(Dispatchers.IO) {
                    return@withContext MarvelLogic().getInitChars(limit, offset)
                }

                this@ThirdFragment.offset +=limit
                rvAdapter.items = marvelCharsItems
                binding.rvMarvelChars3.apply {
                    this.adapter = rvAdapter
                    this.layoutManager = lManager
                    // this.layoutManager = gManager//para hacer 2 columnas
                }
            }
        } else {
            //Snackbar.make(requireContext(), "No hay conexion", Snackbar.LENGTH_LONG).show()
            Toast.makeText(requireContext(), "No hay conexion", Toast.LENGTH_SHORT).show()
        }
    }
}
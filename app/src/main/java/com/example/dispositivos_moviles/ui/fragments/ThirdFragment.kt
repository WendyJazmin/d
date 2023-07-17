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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ThirdFragment : Fragment() {
    private lateinit var binding: FragmentThirdBinding

    private lateinit var lmanager: LinearLayoutManager
    private lateinit var rvAdapter: MarvelAdapter3

    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf<MarvelChars>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThirdBinding.inflate(
            layoutInflater, container, false
        )
        lmanager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        return binding.root
    }

    private fun sendMarvelItem(item: MarvelChars) {
        val i = Intent(requireActivity(), DetailsMarvelItem::class.java)
        i.putExtra("name", item)
        startActivity(i)
    }
    override fun onStart() {
        super.onStart()
        //carga los datos
//        chargeDataRV("cap")

        binding.rvSwipe3.setOnRefreshListener {
//            chargeDataRV("cap")
            binding.rvSwipe3.isRefreshing = false
        }
        binding.rvMarvelChars3.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val v = lmanager.childCount
                    val p = lmanager.findFirstVisibleItemPosition()
                    val t = lmanager.itemCount
                    if ((v + p) >= t) {
                        lifecycleScope.launch((Dispatchers.IO)) {
                            val newItems = JikanAnimeLogic().getAllAnimes()
                            withContext(Dispatchers.Main) {
                                rvAdapter.updateListItems(newItems)
                            }
                        }
                    }
                }
            }
        })

        //nos sirve para filtrar informacion
        binding.txtFilter3.addTextChangedListener {
            chargeDataRV(binding.txtFilter3.text.toString())
        }
    }

    private fun chargeDataRV(search: String) {

        lifecycleScope.launch(Dispatchers.Main) {
            marvelCharsItems = withContext(Dispatchers.IO) {
                return@withContext (MarvelLogic().getMarvelChars(
                    search, 99
                ))
            }
            rvAdapter = MarvelAdapter3(marvelCharsItems, fnClick = { sendMarvelItem(it) })
            binding.rvMarvelChars3.apply {
                this.adapter = rvAdapter
                this.layoutManager = lmanager
            }
        }
    }
}
    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentThirdBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
    override fun onStart(){
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

        binding.spinner3.adapter = adapter
        binding.listView3.adapter = adapter
    }*/

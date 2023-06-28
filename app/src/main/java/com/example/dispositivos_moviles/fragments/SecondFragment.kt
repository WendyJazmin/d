package com.example.dispositivos_moviles.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.activities.DetailsMarvelItem
import com.example.dispositivos_moviles.adapters.MarvelAdapter
import com.example.dispositivos_moviles.databinding.FragmentFirstBinding
import com.example.dispositivos_moviles.databinding.FragmentSecondBinding
import com.example.dispositivos_moviles.lists.ListItems
import com.example.dispositivos_moviles.logic.jikanLogic.JikanAnimeLogic
import com.example.dispositivos_moviles.marvel.MarvelChars
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(layoutInflater, container, false)

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

        binding.spinner2.adapter = adapter
        chargeDataRV()

        binding.rvSwipe.setOnRefreshListener {//cargando
            chargeDataRV()
            binding.rvSwipe.isRefreshing = false
        }
    }

    fun sendMarvelItem(item: MarvelChars) {
        //Intents solo estan en fragments y activities
        val i = Intent(requireActivity(), DetailsMarvelItem::class.java)
        i.putExtra("name", item)
        startActivity(i)
    }

    fun chargeDataRV() {


        lifecycleScope.launch(Dispatchers.IO){
            val rvAdapter = MarvelAdapter(
                //ListItems().returnMarvelChars()
                JikanAnimeLogic().getAllAnimes()
            )
            //las funciones lambda se llaman con {} y van fuera del parentesis
            { sendMarvelItem(it) }

            withContext(Dispatchers.Main){
                with(binding.rvMarvelChars2){
                    this.adapter = rvAdapter
                    this.layoutManager = LinearLayoutManager(
                        requireActivity(),
                        androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                        false
                    )
                }
            }

        }

    }
}

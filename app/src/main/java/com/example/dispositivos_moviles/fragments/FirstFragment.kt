package com.example.dispositivos_moviles.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.adapters.MarvelAdapter
import com.example.dispositivos_moviles.databinding.FragmentFirstBinding
import com.example.dispositivos_moviles.lists.ListItems


class FirstFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFirstBinding.inflate(layoutInflater, container, false)

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

        binding.spinner.adapter = adapter
        val rvAdapter = MarvelAdapter(ListItems().returnMarvelChars())
        val rvMarvel = binding.rvMarvelChars
        rvMarvel.adapter = rvAdapter
        rvMarvel.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

}
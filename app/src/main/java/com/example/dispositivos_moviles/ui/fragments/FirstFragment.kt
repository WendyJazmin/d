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
import com.example.dispositivos_moviles.ui.activities.DetailsMarvelItem
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter
import com.example.dispositivos_moviles.databinding.FragmentFirstBinding
import com.example.dispositivos_moviles.logic.jikanLogic.JikanAnimeLogic
import com.example.dispositivos_moviles.logic.marvelLogic.MarvelLogic
import com.example.dispositivos_moviles.logic.data.MarvelChars
import com.example.dispositivos_moviles.ui.adapters.MarvelAdapter2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private lateinit var lmanager: LinearLayoutManager
    private var rvAdapter : MarvelAdapter = MarvelAdapter { sendMarvelItem(it) }
    private var page = 1
    private var marvelCharsItems: MutableList<MarvelChars> = mutableListOf<MarvelChars>()

    //para hacer en dos columnas
    private lateinit var gManager: GridLayoutManager

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
            "Rosa")

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
        binding.rvSwipe.setOnRefreshListener {
            chargeDataRVDB(5)
            binding.rvSwipe.isRefreshing = false
            lmanager.scrollToPositionWithOffset(5, 30)
        }

        binding.rvMarvelChars.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(
                        recyclerView,
                        dx,
                        dy
                    )//dy para abajo contando y mostrando, y la dx de izquierda a derecha
                    val v = lmanager.childCount//cuantos elementos tengo
                    val p = lmanager.findFirstVisibleItemPosition()//cual es mi posicion actual
                    val t = lmanager.itemCount//cuantos tengo en toal

                    if (dy > 0) {
                        if ((v + p) >= t) {
                            lifecycleScope.launch((Dispatchers.IO)) {
                                /*  val newItems = JikanAnimeLogic().getAllAnimes()*/
                                val newItems = MarvelLogic().getMarvelChars(
                                    name = "spider",
                                    limit = 20
                                )
                                withContext(Dispatchers.Main) {
                                    rvAdapter.updateListItems((newItems))
                                }

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

            if(marvelCharsItems.isEmpty()){
                marvelCharsItems = withContext(Dispatchers.IO){
                    return@withContext(MarvelLogic().getAllMarvelChars(0, page * 3)).toMutableList()
                }
            }

            withContext(Dispatchers.IO){
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

}


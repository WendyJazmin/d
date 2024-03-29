package com.example.dispositivos_moviles.ui.activities


import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dispositivos_moviles.R

import com.example.dispositivos_moviles.databinding.ActivitySecondBinding
import com.example.dispositivos_moviles.ui.fragments.FirstFragment
import com.example.dispositivos_moviles.ui.fragments.SecondFragment
import com.example.dispositivos_moviles.ui.fragments.ThirdFragment
import com.example.dispositivos_moviles.ui.utilities.FragmentsManager


class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_second)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        var name : String = ""

        binding.txtView.text = "Bienvenido $name"
        Log.d("UCE", "Entrando a Start")

        //super.onStart()
       /* FragmentsManager().replaceFragment(supportFragmentManager,
            binding.frmContainer.id, FirstFragment()
        )*/


        initClass()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    fun initClass(){
        /*Log.d("uce", "Entrando a start")  debug en la terminal*/
      /*  binding.btnRetorno.setOnClickListener{
            Log.d("UCE", "Entrando al click de retorno")
            var intent= Intent(this, ActivityMainBinding::class.java)
            startActivity(intent)

            /*Snackbar.make(
                binding.loginSegundo,"regresando",
                Snackbar.LENGTH_LONG).show()*/
        }
*/
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.inicio -> {
                    FragmentsManager().replaceFragment(supportFragmentManager,
                        binding.frmContainer.id, FirstFragment()
                    )
                    true
                }
                R.id.favoritos -> {
                    FragmentsManager().replaceFragment(supportFragmentManager,
                        binding.frmContainer.id, SecondFragment()
                    )
                    true
                }
                R.id.apis -> {
                    FragmentsManager().replaceFragment(supportFragmentManager,
                        binding.frmContainer.id, ThirdFragment()
                    )
                    true
                }
                else -> false
            }
        }
    }
}
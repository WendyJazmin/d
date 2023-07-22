package com.example.dispositivos_moviles.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.databinding.ActivityResultBinding

//martes 18 de julio
import android.content.Intent


class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.btnResultOk.setOnClickListener{
            val i = Intent()
            i.putExtra("result", "Resultado exitoso")
            setResult(RESULT_OK, i)
            finish()//termina la activity, se ejecuta el onDestroy
        }

        binding.btnResultFalse.setOnClickListener{
            val i = Intent()
            i.putExtra("result", "Resultado fallido")
            setResult(RESULT_CANCELED, i)
            finish()//termina la activity, se ejecuta el onDestroy
        }
    }
}
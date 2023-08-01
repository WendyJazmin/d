package com.example.dispositivos_moviles.ui.activities

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.databinding.ActivityNotificationBinding

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import com.example.dispositivos_moviles.ui.utilities.BroadcasterNotifications
import java.util.Calendar


class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNotification.setOnClickListener {
            createNotificationChannel()
            sendNotification()
        }

        binding.btnNotificationProgramada.setOnClickListener{
            val calendar=Calendar.getInstance()
            val hora=binding.timePicker.hour
            val minutes=binding.timePicker.minute
            Toast.makeText(
                this,
                "La notificacion se activara a las: $hora:$minutes",
                Toast.LENGTH_SHORT
            ).show()
            calendar.set(Calendar.HOUR,hora)
            calendar.set(Calendar.MINUTE,minutes)
            calendar.set(Calendar.SECOND,0)
            sendNotificationTimePicker(calendar.timeInMillis)
        }
    }

    //Nota: Las fechas siempre se convierten en numeros tipo Long
    private fun sendNotificationTimePicker(time:Long) {
        //Desde cualquier parte de la aplicacion hacia el broadcast
        val myIntent= Intent(applicationContext, BroadcasterNotifications::class.java)
        val myPendingIntent=PendingIntent.getBroadcast(
            applicationContext,
            0,
            myIntent,
            //Estas banderas van a decir que va a pasar cuando se abra el intent
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        //Alarm Manager nos va a ayudar a que el sistema se levante
        val alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,myPendingIntent)
    }

    val CHANNEL : String = "Notificaciones"

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Variedades"
            val descriptionText = "Notificaciones simples de variedades"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendNotification() {
        //crea la notificacion
        val noti = NotificationCompat.Builder(this, CHANNEL)
        noti.setContentTitle("Primera notificacion")
        noti.setContentText("Tienes una notificacion")
        noti.setSmallIcon(R.drawable.twitter_15)
        noti.setPriority(NotificationCompat.PRIORITY_HIGH)
        noti.setStyle(NotificationCompat.BigTextStyle()
            .bigText("Esta es una notificacion para recordar que estamos trabajando en Android"))

        //envia la notificacion
        with(NotificationManagerCompat.from(this)){
            notify(1, noti.build())
        }
    }
}
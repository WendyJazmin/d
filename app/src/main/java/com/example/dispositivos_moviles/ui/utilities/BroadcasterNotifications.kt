package com.example.dispositivos_moviles.ui.utilities

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.dispositivos_moviles.R
import com.example.dispositivos_moviles.ui.activities.CameraActivity

class BroadcasterNotifications:BroadcastReceiver() {
    val CHANNEL: String ="Notificaciones"
    override fun onReceive(context: Context, intent: Intent) {
        val myIntent=Intent(context, CameraActivity::class.java)
        val myPendingIntent= PendingIntent.getBroadcast(
            context,
            0,
            myIntent,
            //Estas banderas van a decir que va a pasar cuando se abra el intent
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val not = NotificationCompat.Builder(context, CHANNEL)
        not.setContentTitle("Primera notificacion")
        not.setContentText("Tienes una notificacion")
        //El icono que se muestra en la barra de notificaciones
        not.setSmallIcon(R.drawable.favorite_24)
        //Imagen que aparece dentro de la notificacion
//        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.eva)
//        not.setLargeIcon(bitmap)
        not.setPriority(NotificationCompat.PRIORITY_MAX)
        not.setStyle(NotificationCompat.BigTextStyle().bigText("Esta es una notificacion para recordar que estamos trabajando en android"))
        not.setContentIntent(myPendingIntent)
        val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            1,
            not.build()
        )
    }

}
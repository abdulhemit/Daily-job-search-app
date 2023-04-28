package com.example.gunlukis.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.gunlukis.R
import com.example.gunlukis.activities.MainActivity
import com.example.gunlukis.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService() {

    private lateinit var workerList: MutableList<User>
    private lateinit var bossList: MutableList<User>
    private var channelId = "yeni mesaj"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {

        var bildirimBaslik = message.notification?.title
        var bildirimBody = message.notification?.body
        var bildirimCesilenUserId = message.data.get("secilenUserId")
        var bildirimMesajGonderen = message.data.get("mesajGonderen")


        Log.e("FCM","Baslik: $bildirimBaslik govde: $bildirimBody data: $bildirimCesilenUserId")
        println("Baslik: $bildirimBaslik govde: $bildirimBody data: $bildirimCesilenUserId")

        yeniMesajBildirimiGoster(bildirimBaslik,bildirimBody,bildirimCesilenUserId,bildirimMesajGonderen)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun yeniMesajBildirimiGoster(
        bildirimBaslik: String?,
        bildirimBody: String?,
        gidilecekUserId: String?,
        bildirimMesajGonderen: String?
    ) {


        var pendingIntent = Intent(this,MainActivity::class.java)
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("secilenUserId",gidilecekUserId)
        pendingIntent.putExtra("mesajGonderenKisi",bildirimMesajGonderen)

        var bildirimPendingIntent = PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)


        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }
        var builder = Notification.Builder(this,channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_notifications))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(bildirimBaslik)
            .setContentText(bildirimBody)
            .setAutoCancel(true)
            .setContentIntent(bildirimPendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(),builder)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){

        val channelName = "channelName"
        val channel = NotificationChannel(channelId,channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My channel discription"
            enableLights(true)
            lightColor = Color.GRAY

        }
        notificationManager.createNotificationChannel(channel)

    }

    override fun onNewToken(token: String) {

        if (FirebaseAuth.getInstance().currentUser != null){
            WorkersUsers()
            BossUsers()
            WorkerYeniTokenKaydetme(token)
        }

    }

    private fun WorkerYeniTokenKaydetme(newToken: String) {

        for(id in (workerList as ArrayList<String>)) {

            if (FirebaseAuth.getInstance().currentUser!!.uid == id) {
                FirebaseDatabase.getInstance().reference
                    .child("workers")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("fcm_token").setValue(newToken)

            }else{
                BossYeniTokenKaydetme(newToken)
            }
        }


    }

    private fun BossYeniTokenKaydetme(newToken: String) {

        for(id in (bossList as ArrayList<String>)){

            if (FirebaseAuth.getInstance().currentUser!!.uid == id) {
                FirebaseDatabase.getInstance().reference
                    .child("bosses")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("fcm_token").setValue(newToken)

            }
        }

    }


    private fun WorkersUsers() {

        workerList = ArrayList()

        val workers = FirebaseDatabase.getInstance().reference
            .child("workers")


        workers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (workerList as ArrayList<String>).add(it) }
                    }


                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }
    private fun BossUsers() {

        bossList = ArrayList()

        val workers = FirebaseDatabase.getInstance().reference
            .child("bosses")


        workers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (bossList as ArrayList<String>).add(it) }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }
}
package com.example.gunlukis.services

import android.util.Log
import android.view.View
import com.example.gunlukis.fragments.homeFragment
import com.example.gunlukis.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService() {

    private lateinit var workerList: MutableList<User>
    private lateinit var bossList: MutableList<User>


    override fun onMessageReceived(message: RemoteMessage) {

        var bildirimBaslik = message.notification?.title
        var bildirimBody = message.notification?.body
        var bildirimData = message.data

        Log.e("FCM","Baslik: $bildirimBaslik govde: $bildirimBody data: $bildirimData")
        println("Baslik: $bildirimBaslik govde: $bildirimBody data: $bildirimData")


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
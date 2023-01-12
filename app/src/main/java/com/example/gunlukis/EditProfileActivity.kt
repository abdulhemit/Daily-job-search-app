package com.example.gunlukis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.gunlukis.databinding.ActivityEditProfileBinding
import com.example.gunlukis.databinding.FragmentProfileBinding
import com.example.gunlukis.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var workerList: MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        WorkersUsers()

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

                    for(id in (workerList as ArrayList<String>)) {

                        if (auth.currentUser!!.uid == id) {
                            getUserWorker()

                        }else{
                            BossUsers()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }

    private fun getUserWorker() {

        val usersRaf = database.reference.child("workers").child(auth.currentUser!!.uid)

        usersRaf.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        binding.idAdSoyAd.setText(it?.userName)
                        Picasso.get().load(it?.image).into(binding.imageProfileEditProfileActivity)

                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun BossUsers() {

        workerList = ArrayList()

        val workers = FirebaseDatabase.getInstance().reference
            .child("bosses")


        workers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (workerList as ArrayList<String>).add(it) }
                    }

                    for(id in (workerList as ArrayList<String>)) {

                        if (auth.currentUser!!.uid == id) {
                            getUserBoss()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }

    private fun getUserBoss() {

        val usersRaf = database.reference.child("bosses").child(auth.currentUser!!.uid)

        usersRaf.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        binding.idAdSoyAd.setText(it?.userName)
                        Picasso.get().load(it?.image).into(binding.imageProfileEditProfileActivity)
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}
package com.example.gunlukis.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager

import com.example.gunlukis.adapter.MyJobAdapter
import com.example.gunlukis.databinding.ActivityBugunkuilanlarBinding
import com.example.gunlukis.models.PostJob
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class bugunkuilanlarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBugunkuilanlarBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var postJobList: MutableList<PostJob>
    private lateinit var MyjobAdapter: MyJobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBugunkuilanlarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()


        postJobList = ArrayList()
        val fragmentmanager = supportFragmentManager
        MyjobAdapter = MyJobAdapter(postJobList,fragmentmanager)




        val linearLayoutManager = LinearLayoutManager(this)



        // listeyi alttan dizme
        //linearLayoutManager.reverseLayout = true
        binding?.bugunkuIlan?.layoutManager = linearLayoutManager
        binding?.bugunkuIlan?.adapter = MyjobAdapter

        MyjobAdapter.notifyDataSetChanged()
        getPostJobCurrentUser()
    }

    private fun getPostJobCurrentUser() {

        val postRef = database.reference
            .child("myPostJob")
            .child(auth.currentUser!!.uid)
        postRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    postJobList.clear()

                    for (snap in snapshot.children){
                        val postList = snap.getValue<PostJob>(PostJob::class.java)

                        postList.let {
                            it?.worker = false
                            postJobList.add(it!!)


                        }

                        if(postList?.uid == null){
                            binding.bugunkuIlan.visibility = View.INVISIBLE
                            binding.henuzMesajYokId.visibility = View.VISIBLE
                        }else {
                            binding.bugunkuIlan.visibility = View.VISIBLE
                            binding.henuzMesajYokId.visibility = View.INVISIBLE
                        }

                    }

                    MyjobAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
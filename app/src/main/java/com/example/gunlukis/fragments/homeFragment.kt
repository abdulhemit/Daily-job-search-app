package com.example.gunlukis.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gunlukis.adapter.JobAdapter
import com.example.gunlukis.databinding.FragmentHomeBinding
import com.example.gunlukis.models.PostJob
import com.example.gunlukis.models.User
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class homeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var postJobList: MutableList<PostJob>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var workerList: MutableList<User>
    private lateinit var bossList: MutableList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding?.root

        postJobList = ArrayList()
        val fragmentmanager = fragmentManager
        jobAdapter = JobAdapter(postJobList,fragmentmanager!!)


        val linearLayoutManager = LinearLayoutManager(requireContext())

        // listeyi alttan dizme
        //linearLayoutManager.reverseLayout = true
        binding?.homeFragmentRecyclirview?.layoutManager = linearLayoutManager
        binding?.homeFragmentRecyclirview?.adapter = jobAdapter

        jobAdapter.notifyDataSetChanged()
        BossUsers()
        WorkersUsers()
        return binding?.root
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

                    for(id in (bossList as ArrayList<String>)) {

                        if (auth.currentUser!!.uid == id) {
                            binding?.bosYayinladigimIlanlar?.visibility = View.VISIBLE
                            getPostJobCurrentUser()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
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

                    }

                    jobAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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
                            binding?.bosYayinladigimIlanlar?.visibility = View.GONE
                            getPostJob()

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



    private fun getPostJob() {

        val postRef = database.reference.child("PostJob")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    postJobList.clear()
                    for (snap in snapshot.children){
                        val postList = snap.getValue<PostJob>(PostJob::class.java)
                        postList.let {
                            it?.worker = true
                            println("homeIlanYeri ${it?.yer}")
                            postJobList.add(it!!)

                        }

                    }

                    jobAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}
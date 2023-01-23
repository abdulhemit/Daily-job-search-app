package com.example.gunlukis.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gunlukis.adapter.JobAdapter
import com.example.gunlukis.databinding.FragmentHomeBinding
import com.example.gunlukis.models.PostJob
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class homeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var postJobList: MutableList<PostJob>
    private lateinit var jobAdapter: JobAdapter


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
        jobAdapter = JobAdapter(postJobList)
        val linearLayoutManager = LinearLayoutManager(requireContext())

        // listeyi alttan dizme
        //linearLayoutManager.reverseLayout = true
        binding?.homeFragmentRecyclirview?.layoutManager = linearLayoutManager
        binding?.homeFragmentRecyclirview?.adapter = jobAdapter


        getPostJob()
        return binding?.root
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
                               postJobList.add(postList!!)
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
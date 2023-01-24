package com.example.gunlukis.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.SharedElementCallback
import com.example.gunlukis.R
import com.example.gunlukis.databinding.FragmentHomeBinding
import com.example.gunlukis.databinding.FragmentIlanDetayiBinding
import com.example.gunlukis.models.PostJob
import com.example.gunlukis.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class IlanDetayiFragment : Fragment() {
    private var _binding : FragmentIlanDetayiBinding? = null
    private val binding get() = _binding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var postjobList: MutableList<User>
    private lateinit var postId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIlanDetayiBinding.inflate(inflater, container, false)
        val view = binding?.root
        val arg = arguments
        arg.let {
            postId = arg?.getString("postId").toString()
        }
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer,homeFragment())?.commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        getPostJobsIdLinst()


        return binding?.root
    }
    private fun getPostJobsIdLinst(){

        postjobList = ArrayList()
        val postRef = database.reference
            .child("PostJob")
            //.child(postId)


        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (postjobList as ArrayList<String>).add(it) }

                    }
                    postjobList

                    for(id in (postjobList as ArrayList<String>)) {

                        if (postId == id) {

                            getPostJobs()
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun getPostJobs(){

        postjobList = ArrayList()
        val postRef = database.reference
            .child("PostJob")
            .child(postId)


        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val postjob = snapshot.getValue<PostJob>(PostJob::class.java)
                    postjob.let {

                        binding?.isIlaniId?.setText(postjob?.ilanAdi)
                        binding?.isFiyatiId?.setText(postjob?.ilanFiyati)
                        binding?.isAciklamaId?.setText(postjob?.ilanAciklama)

                    }

                    /*
                    for (snap in snapshot.children){


                    }

                     */

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }



}
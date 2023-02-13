package com.example.gunlukis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gunlukis.adapter.UserAdapter
import com.example.gunlukis.adapter.konusmalarAdapter
import com.example.gunlukis.databinding.FragmentMessageBinding
import com.example.gunlukis.models.User
import com.example.gunlukis.models.chat
import com.example.gunlukis.models.konusmalar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class messageFragment : Fragment() {
    private var _binding : FragmentMessageBinding? = null
    private val binding get() = _binding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var konusmalarList: MutableList<konusmalar>
    private lateinit var userList: MutableList<User>
    private lateinit var konusmalaradapter: konusmalarAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        konusmalarList = ArrayList()




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val view = binding?.root
        userList = ArrayList()
        konusmalaradapter = konusmalarAdapter(konusmalarList)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding?.RecyclerviewFragmentMessage?.layoutManager = linearLayoutManager
        binding?.RecyclerviewFragmentMessage?.adapter = konusmalaradapter
        konusmalariGetirmek()

        return binding?.root
    }
    private fun konusmalariGetirmek(){

        database.reference.child("konusmalar").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    konusmalarList.clear()
                    if(snapshot.exists()){
                        for (snap in snapshot.children){

                            var konusmalar = snap.getValue(konusmalar::class.java)
                            konusmalar?.userId = snap.key
                            konusmalar.let {
                                konusmalarList.add(konusmalar!!)

                            }
                        }
                        konusmalaradapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

}
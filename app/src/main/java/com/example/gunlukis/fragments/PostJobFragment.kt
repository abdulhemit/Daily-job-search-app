package com.example.gunlukis.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gunlukis.R
import com.example.gunlukis.databinding.FragmentPostJobBinding
import com.example.gunlukis.databinding.FragmentProfileBinding
import com.example.gunlukis.models.PostJob
import com.example.gunlukis.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import okhttp3.internal.Util


class PostJobFragment : Fragment() {
    private var _binding : FragmentPostJobBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostJobBinding.inflate(inflater, container, false)
        val view = binding.root


        /*
        val postRef = database.reference.child("PostJob")
            postRef.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){

                        val postJob = snapshot.getValue<PostJob>(PostJob::class.java)
                        for (snap in snapshot.children){
                            val ilanAdi = snap.getValue(PostJob::class.java)
                            binding.isIlaniId.setText(ilanAdi?.ilanAdi)
                            binding.isFiyatiId.setText(ilanAdi?.ilanFiyati)
                            binding.isAciklamaId.setText(ilanAdi?.ilanAciklama)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })



         */

        binding.IdYayinla.setOnClickListener {

            when{
                TextUtils.isEmpty(binding.isIlaniId.text) -> Toast.makeText(requireContext(),"iş ilan adını giriniz",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(binding.isFiyatiId.text) -> Toast.makeText(requireContext(),"iş fiyatını giriniz",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(binding.isAciklamaId.text) -> Toast.makeText(requireContext(),"gereken açıklamaları yazınız",Toast.LENGTH_LONG).show()

                else -> {
                    val postJobMap = HashMap<String,Any>()
                    val currentId = auth.currentUser!!.uid
                    postJobMap["ilanAdi"] = binding.isIlaniId.text.toString()
                    postJobMap["ilanFiyati"] = binding.isFiyatiId.text.toString()
                    postJobMap["ilanAciklama"] = binding.isAciklamaId.text.toString()
                    postJobMap["uid"] = currentId

                    val postRef = database.reference.child("PostJob")
                    val pushkey = postRef.push().key
                    postRef.child(pushkey!!).setValue(postJobMap).addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Toast.makeText(requireContext(),"Basarili",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(requireContext(),"Hata",Toast.LENGTH_LONG).show()

                        }
                    }

                }
            }
        }


        return binding.root
    }


}
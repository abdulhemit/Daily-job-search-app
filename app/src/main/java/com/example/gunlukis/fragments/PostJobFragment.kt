package com.example.gunlukis.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gunlukis.R
import com.example.gunlukis.activities.MainActivity
import com.example.gunlukis.databinding.FragmentPostJobBinding
import com.example.gunlukis.databinding.FragmentProfileBinding
import com.example.gunlukis.models.PostJob
import com.example.gunlukis.models.User
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import okhttp3.internal.Util
import java.util.UUID


class PostJobFragment : Fragment() {
    private var _binding : FragmentPostJobBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var uuid: UUID


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

        binding.IdYayinla.setOnClickListener {

            saveJobs()

        }


        return binding.root
    }
    private fun saveJobs(){

        when{
            TextUtils.isEmpty(binding.isIlaniId.text) -> Toast.makeText(requireContext(),"iş ilan adını giriniz",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.isFiyatiId.text) -> Toast.makeText(requireContext(),"iş fiyatını giriniz",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.isAciklamaId.text) -> Toast.makeText(requireContext(),"gereken açıklamaları yazınız",Toast.LENGTH_LONG).show()

            else -> {
                 uuid = UUID.randomUUID()
                val postJobMap = HashMap<String,Any>()
                val currentId = auth.currentUser!!.uid
                postJobMap["ilanAdi"] = binding.isIlaniId.text.toString()
                postJobMap["ilanFiyati"] = binding.isFiyatiId.text.toString()
                postJobMap["ilanAciklama"] = binding.isAciklamaId.text.toString()
                postJobMap["uid"] = currentId
                postJobMap["postId"] = uuid.toString()
                postJobMap["isAdresi"] = binding.isYeriId.text.toString()
                postJobMap["calismaSaati"] = binding.isSaatiId.text.toString()

                val postRef = database.reference.child("PostJob")
                postRef.child(uuid.toString()).setValue(postJobMap).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        saveMyJobs()

                    }else{
                        Toast.makeText(requireContext(),"Hata",Toast.LENGTH_LONG).show()

                    }
                }

            }
        }
    }

    private fun saveMyJobs(){

        val postJobMap = HashMap<String,Any>()
        val currentId = auth.currentUser!!.uid
        postJobMap["ilanAdi"] = binding.isIlaniId.text.toString()
        postJobMap["ilanFiyati"] = binding.isFiyatiId.text.toString()
        postJobMap["ilanAciklama"] = binding.isAciklamaId.text.toString()
        postJobMap["uid"] = currentId
        postJobMap["postId"] = uuid.toString()
        postJobMap["isYeri"] = binding.isYeriId.text.toString()
        postJobMap["calismaSaati"] = binding.isSaatiId.text.toString()

        val myPostRef = database.reference.child("myPostJob")
        myPostRef.child(auth.currentUser!!.uid.toString()).child(uuid.toString()).setValue(postJobMap).addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(requireContext(),"ilan yayınlandı",Toast.LENGTH_LONG).show()
               startActivity(Intent(requireContext(),MainActivity::class.java))
            }else{
                Toast.makeText(requireContext(),"Hata",Toast.LENGTH_LONG).show()

            }
        }
    }



}
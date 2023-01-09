package com.example.gunlukis.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gunlukis.HangiKullaniciActivity
import com.example.gunlukis.R
import com.example.gunlukis.databinding.FragmentHomeBinding
import com.example.gunlukis.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class profileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.out.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(),HangiKullaniciActivity::class.java))

        }
        return binding.root
    }


}
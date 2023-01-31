package com.example.gunlukis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gunlukis.databinding.ActivityHangiKullaniciBinding
import com.google.firebase.auth.FirebaseAuth


class HangiKullaniciActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHangiKullaniciBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHangiKullaniciBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = binding
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null){
            startActivity(Intent(this@HangiKullaniciActivity, MainActivity::class.java))
            finish()
        }
        binding.IsAriyorum.setOnClickListener {
            startActivity(Intent(this@HangiKullaniciActivity, isAriyorumGiris::class.java))
        }
        binding.ElemanAriyorum.setOnClickListener {
            startActivity(Intent(this@HangiKullaniciActivity, elemanAriyorumActivity::class.java))
        }
    }
}
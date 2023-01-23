package com.example.gunlukis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gunlukis.databinding.ActivityHangiKullaniciBinding


class HangiKullaniciActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHangiKullaniciBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHangiKullaniciBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = binding
        binding.IsAriyorum.setOnClickListener {
            startActivity(Intent(this@HangiKullaniciActivity, isAriyorumGiris::class.java))
        }
        binding.ElemanAriyorum.setOnClickListener {
            startActivity(Intent(this@HangiKullaniciActivity, elemanAriyorumActivity::class.java))
        }
    }
}
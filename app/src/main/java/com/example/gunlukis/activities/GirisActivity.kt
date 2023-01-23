package com.example.gunlukis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.gunlukis.R
import com.example.gunlukis.databinding.ActivityGirisBinding
import com.google.firebase.auth.FirebaseAuth
import com.sarnava.textwriter.TextWriter

class girisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGirisBinding
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGirisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //auth.signOut()



        binding.textWriter
            .setWidth(12f)
            .setDelay(4)
            .setColor(resources.getColor(R.color.black))
            .setConfig(TextWriter.Configuration.INTERMEDIATE)
            .setSizeFactor(22f)
            .setLetterSpacing(25f)
            .setText("WELCOME TO THE APP")
            .startAnimation()

        runnable = Runnable{
            auth = FirebaseAuth.getInstance()

            if (auth.currentUser != null){
                startActivity(Intent(this@girisActivity, MainActivity::class.java))
                finish()
            }else{

            startActivity(Intent(this@girisActivity, HangiKullaniciActivity::class.java))
            finish()

            }

        }
        handler = Handler()
        handler.postDelayed(runnable,8000)
    }

}
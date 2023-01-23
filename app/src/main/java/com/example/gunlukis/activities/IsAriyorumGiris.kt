package com.example.gunlukis.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.gunlukis.R
import com.example.gunlukis.databinding.ActivityIsAriyorumGirisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class isAriyorumGiris : AppCompatActivity() {
    private lateinit var binding: ActivityIsAriyorumGirisBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIsAriyorumGirisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = binding
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //bosses

        binding.singUp.setOnClickListener {
            binding.singUp.background = resources.getDrawable(R.drawable.switch_trcks,null)
            binding.singUp.setTextColor(resources.getColor(R.color.white))
            binding.logIn.background = null
            binding.logIn.setTextColor(resources.getColor(R.color.pickColor))
            binding.SingUpLayout.visibility = View.VISIBLE
            binding.loginLayout.visibility = View.GONE
        }

        binding.logIn.setOnClickListener {
            binding.logIn.background = resources.getDrawable(R.drawable.switch_trcks,null)
            binding.logIn.setTextColor(resources.getColor(R.color.white))
            binding.singUp.background = null
            binding.singUp.setTextColor(resources.getColor(R.color.pickColor))
            binding.SingUpLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
        }

        binding.singIn.setOnClickListener {

            if (binding.SingUpLayout.visibility == View.VISIBLE && binding.loginLayout.visibility == View.GONE){
                CreatAccount()
            }else{
                loginUser()
            }
        }

    }

    private fun loginUser() {
        when{

            TextUtils.isEmpty(binding.eMail.text) -> Toast.makeText(this,"Email gerekli",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.passwords.text) -> Toast.makeText(this,"Şifre gerekli",Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@isAriyorumGiris)
                progressDialog.setTitle("Giriş yap")
                progressDialog.setMessage("Lütfen bekleyin, Bu biraz zaman alabilir...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                auth.signInWithEmailAndPassword(binding.eMail.text.toString(),binding.passwords.text.toString())
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
                            progressDialog.dismiss()
                            startActivity(Intent(this@isAriyorumGiris, MainActivity::class.java))
                            finish()
                        }else{

                            progressDialog.dismiss()
                            val message = task.exception.toString()
                            Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun CreatAccount() {

        when {
            TextUtils.isEmpty(binding.singUpEMail.text) -> Toast.makeText(this,"Email gerekli",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.SingUpPasswords.text) -> Toast.makeText(this,"Şifre gerekli",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.SingUPPasswords01.text) -> Toast.makeText(this,"Şifre gerekli",Toast.LENGTH_LONG).show()
            //TextUtils.equals(binding.SingUpPasswords.text,binding.SingUPPasswords01.text) -> Toast.makeText(this,"Şifre aynı değil!",Toast.LENGTH_LONG).show()

            else -> {
                if (binding.SingUpPasswords.text.toString() == binding.SingUPPasswords01.text.toString()){

                    val progressDialog = ProgressDialog(this@isAriyorumGiris)
                    progressDialog.setTitle("Üye Ol")
                    progressDialog.setMessage("Lütfen bekleyin, Bu biraz zaman alabilir...")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    auth.createUserWithEmailAndPassword(binding.singUpEMail.text.toString(),binding.SingUpPasswords.text.toString())
                        .addOnCompleteListener { task->
                            if (task.isSuccessful){

                                SaveUserInfo(progressDialog)

                            }else{

                                val message = task.exception.toString()
                                Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                                auth.signOut()
                                progressDialog.dismiss()
                            }
                        }
                }else{

                    Toast.makeText(this,"Şifre aynı değil!",Toast.LENGTH_LONG).show()
                }

            }

        }

    }

    private fun SaveUserInfo(progressDialog:ProgressDialog) {
        val currentUserId = auth.currentUser!!.uid
        val usersRaf = database.reference.child("workers")
        val userMap = HashMap<String,Any>()
        userMap["uid"] = currentUserId
        userMap["userName"] = binding.singUpKullaniciAdi.text.toString()
        userMap["eMail"] = binding.singUpEMail.text.toString()
        userMap["onYazi"] = "Günlül iş arama uygulaması kullanıyorum"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/gunlukhizmet.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=16557ef4-02b0-4f16-81b1-735bb424cc0a"

       usersRaf.child(currentUserId).setValue(userMap)
           .addOnCompleteListener { task->
               if (task.isSuccessful){
                   progressDialog.dismiss()
                   startActivity(Intent(this@isAriyorumGiris, MainActivity::class.java))
                   finish()

               }else{
                   val message = task.exception.toString()
                   Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                   progressDialog.dismiss()
                   auth.signOut()
               }

           }


    }


}
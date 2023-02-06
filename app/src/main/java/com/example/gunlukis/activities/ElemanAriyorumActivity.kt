package com.example.gunlukis.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.gunlukis.R
import com.example.gunlukis.databinding.ActivityElemanAriyorumBinding
import com.example.gunlukis.models.Email
import com.google.common.base.MoreObjects.ToStringHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

class elemanAriyorumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityElemanAriyorumBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var emailList: MutableList<Email>
    private var emailBoolean by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElemanAriyorumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = binding
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        emailList = ArrayList()
        emailBoolean = false

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
                getBossEmail()
            }
        }


    }
    private fun getBossEmail(){

        val emailRaf = database.reference.child("bossesEmail")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){

                        for (snap in snapshot.children){
                            var email = snap.getValue<Email>(Email::class.java)
                            email.let {
                                emailList.add(email!!)
                            }

                        }
                        for (email in (emailList as ArrayList)){

                            if (email.email == binding.eMail.text.toString()){
                                emailBoolean = true
                                loginUser()

                            }
                        }

                        if (emailBoolean == false){
                            Toast.makeText(this@elemanAriyorumActivity,"Email yada şifre yanlış",Toast.LENGTH_SHORT).show()

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun loginUser() {
        when{

            TextUtils.isEmpty(binding.eMail.text) -> Toast.makeText(this,"Email gerekli", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.passwords.text) -> Toast.makeText(this,"Şifre gerekli", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@elemanAriyorumActivity)
                progressDialog.setTitle("Giriş yap")
                progressDialog.setMessage("Lütfen bekleyin, Bu biraz zaman alabilir...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                auth.signInWithEmailAndPassword(binding.eMail.text.toString(),binding.passwords.text.toString())
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
                            progressDialog.dismiss()
                            startActivity(Intent(this@elemanAriyorumActivity, MainActivity::class.java))
                            finish()
                        }else{

                            progressDialog.dismiss()
                            val message = task.exception.toString()
                            Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun CreatAccount() {

        when {
            TextUtils.isEmpty(binding.singUpEMail.text) -> Toast.makeText(this,"Email gerekli",
                Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.SingUpPasswords.text) -> Toast.makeText(this,"Şifre gerekli",
                Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(binding.SingUPPasswords01.text) -> Toast.makeText(this,"Şifre gerekli",
                Toast.LENGTH_LONG).show()

            else -> {
                if (binding.SingUpPasswords.text.toString() == binding.SingUPPasswords01.text.toString()){

                    val progressDialog = ProgressDialog(this@elemanAriyorumActivity)
                    progressDialog.setTitle("Üye Ol")
                    progressDialog.setMessage("Lütfen bekleyin, Bu biraz zaman alabilir...")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    auth.createUserWithEmailAndPassword(binding.singUpEMail.text.toString(),binding.SingUpPasswords.text.toString())
                        .addOnCompleteListener { task->
                            if (task.isSuccessful){
                                Toast.makeText(this@elemanAriyorumActivity,"kayt basarili",Toast.LENGTH_SHORT).show()

                                SaveUserInfo(progressDialog)
                            }else{
                                val message = task.exception.toString()
                                Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                                auth.signOut()
                                progressDialog.dismiss()
                            }
                        }
                }else{

                    Toast.makeText(this,"Şifre aynı değil!", Toast.LENGTH_LONG).show()
                }

            }

        }

    }

    private fun SaveUserInfo(progressDialog: ProgressDialog) {
        auth.currentUser?.uid.let {
            val currentUserId = auth.currentUser?.uid
            val usersRaf = database.reference.child("bosses")
            val userMap = HashMap<String,Any>()
            userMap["uid"] = currentUserId.toString()
            userMap["userName"] = binding.singUpKullaniciAdi.text.toString()
            userMap["eMail"] = binding.singUpEMail.text.toString()
            userMap["onYazi"] = "Günlük eleman arama uygulaması kullanıyorum"
            userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/gunlukhizmet.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=16557ef4-02b0-4f16-81b1-735bb424cc0a"

            usersRaf.child(currentUserId.toString()).setValue(userMap)
                .addOnCompleteListener { task->
                    if (task.isSuccessful){

                        Toast.makeText(this@elemanAriyorumActivity,"kullanici bilgileri kaydi basarili",Toast.LENGTH_LONG).show()
                        saveBossEmail(binding.singUpEMail.text.toString(),progressDialog)

                    }else{
                        val message = task.exception.toString()
                        Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                        println("hata" + message.toString())
                        progressDialog.dismiss()
                        auth.signOut()
                    }

                }
        }



    }
    private fun saveBossEmail(email: String,progressDialog: ProgressDialog){

        val emailRaf = database.reference.child("bossesEmail")
        val emailMap = HashMap<String,Any>()
        emailMap["email"] = email

        emailRaf.push().setValue(emailMap)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    startActivity(Intent(this@elemanAriyorumActivity, MainActivity::class.java))
                    finish()

                }
            }
    }
}
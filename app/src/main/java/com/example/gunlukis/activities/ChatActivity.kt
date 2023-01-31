package com.example.gunlukis.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gunlukis.adapter.ChatActivityAdapter
import com.example.gunlukis.databinding.ActivityChatBinding
import com.example.gunlukis.models.User
import com.example.gunlukis.models.chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore
    private lateinit var bosschatID: String
    private lateinit var workerList: MutableList<User>
    private lateinit var bossList: MutableList<User>
    private lateinit var chatList: MutableList<chat>
    private lateinit var chatActivityAdapter: ChatActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()
        chatList = ArrayList()
        WorkersUsers()
        BossUsers()
        var intent = intent
         bosschatID = intent.getStringExtra("chatID").toString()
        bosschatID.let {
           getUserBossinfo(it)
        }
        chatActivityAdapter = ChatActivityAdapter()
        val linearLayoutManager = LinearLayoutManager(this)
        binding.ChatsRecyclerview.layoutManager = linearLayoutManager
        binding.ChatsRecyclerview.adapter = chatActivityAdapter

        binding.idBackChat.setOnClickListener {
            startActivity(Intent(this@ChatActivity, MainActivity::class.java))
            finish()
        }
        binding.sendMessage.setOnClickListener {

            for (id in (workerList as ArrayList<String>)){

                if (auth.currentUser?.uid == id){
                    CurrentWorkerputChats()
                }
            }
            for (id in (bossList as ArrayList<String>)){

                if (auth.currentUser?.uid == id ){
                   CurrentBoosputChats()
                }
            }

        }



    }


    private fun getWorkerChatMessage(){
        database.reference.child("Chats").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    chatList.clear()
                    for (snap in snapshot.children){
                        val chat = snap.getValue<chat>(chat::class.java)
                        chat.let {
                            println("chat:" + chat?.chat)
                            chatList.add(it!!)
                            chatActivityAdapter.chatList = chatList
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun getUserBossinfo(chatID: String) {
        val usersRaf = database.reference.child("bosses").child(chatID)
        usersRaf.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        binding.idChatName.text = it?.userName
                        Picasso.get().load(it?.image).into(binding.idProfileChat)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun CurrentWorkerputChats(){

        when {
            TextUtils.isEmpty(binding.idMessageText.text) -> Toast.makeText(this,"birşeyler yaz",Toast.LENGTH_LONG).show()

            else->{

                val chatMap = HashMap<String,Any>()
                chatMap["uid"] = auth.currentUser!!.uid
                chatMap["chat"] = binding.idMessageText.text.toString()
                chatMap["time"] = FieldValue.serverTimestamp().toString()

                database.reference.child("Chats").child(auth.currentUser!!.uid).push().setValue(chatMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            bossputChats()
                        }
                    }


            }
        }

    }

    private fun bossputChats(){
        when {
            TextUtils.isEmpty(binding.idMessageText.text) -> Toast.makeText(this,"birşeyler yaz",Toast.LENGTH_LONG).show()

            else->{

                val chatMap = HashMap<String,Any>()
                chatMap["uid"] = bosschatID
                chatMap["chat"] = binding.idMessageText.text.toString()
                chatMap["time"] = FieldValue.serverTimestamp().toString()

                database.reference.child("Chats").child(bosschatID).push().setValue(chatMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            binding.idMessageText.setText("")
                            val bossMap = HashMap<String,Any>()
                            bossMap["BossId"] = bosschatID
                           database.reference.child("BossId").child(bosschatID).setValue(bossMap)
                               .addOnCompleteListener { task->
                                   if (task.isSuccessful){
                                       binding.idMessageText.setText("")
                                   }
                               }
                        }
                    }


            }
        }
    }
    private fun CurrentBoosputChats(){

        when {
            TextUtils.isEmpty(binding.idMessageText.text) -> Toast.makeText(this,"birşeyler yaz",Toast.LENGTH_LONG).show()

            else->{

                val chatMap = HashMap<String,Any>()
                chatMap["uid"] = auth.currentUser!!.uid
                chatMap["chat"] = binding.idMessageText.text.toString()
                chatMap["time"] = FieldValue.serverTimestamp().toString()

                database.reference.child("Chats").child(auth.currentUser!!.uid).push().setValue(chatMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            workerputChats()
                        }
                    }


            }
        }

    }
    private fun workerputChats(){

        when {
            TextUtils.isEmpty(binding.idMessageText.text) -> Toast.makeText(this,"birşeyler yaz",Toast.LENGTH_LONG).show()

            else->{

                val chatMap = HashMap<String,Any>()
                chatMap["uid"] = auth.currentUser!!.uid
                chatMap["chat"] = binding.idMessageText.text.toString()
                chatMap["time"] = FieldValue.serverTimestamp().toString()

                database.reference.child("Chats").child(auth.currentUser!!.uid).push().setValue(chatMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            bossputChats()
                        }
                    }


            }
        }

    }

    private fun WorkersUsers() {

        workerList = ArrayList()

        val workers = FirebaseDatabase.getInstance().reference
            .child("workers")


        workers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (workerList as ArrayList<String>).add(it) }

                    }
                    for (id in (workerList as ArrayList<String>)){

                        if (auth.currentUser?.uid == id){
                            getWorkerChatMessage()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }

    private fun BossUsers() {

        bossList = ArrayList()

        val workers = FirebaseDatabase.getInstance().reference
            .child("bosses")


        workers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (bossList as ArrayList<String>).add(it) }

                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }

}
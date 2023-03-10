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
    private lateinit var workerchatID: String
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
         workerchatID = intent.getStringExtra("workerId").toString()

        bosschatID.let {
           getUserBossinfo(it)
        }
        workerchatID.let {
            getUserWorkerinfo(it)
        }

        chatActivityAdapter = ChatActivityAdapter(chatList)
        val linearLayoutManager = LinearLayoutManager(this)
        //linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
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
        database.reference.child("Chats").child(auth.currentUser!!.uid).child(bosschatID)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    chatList.clear()

                    for (snap in snapshot.children){
                        val chat = snap.getValue<chat>(chat::class.java)
                        chat.let {
                            println("chat:" + chat?.chat)
                            chatList.add(it!!)
                            binding.ChatsRecyclerview.smoothScrollToPosition(chatList.size -1)
                        }

                    }
                    chatActivityAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun getBossChatMessage(){
        database.reference.child("Chats").child(auth.currentUser!!.uid).child(workerchatID)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    chatList.clear()

                    for (snap in snapshot.children){
                        val chat = snap.getValue<chat>(chat::class.java)
                        chat.let {
                            println("chat:" + chat?.chat)
                            chatList.add(it!!)
                            chatActivityAdapter.notifyItemInserted(chatList.size -1)
                            binding.ChatsRecyclerview.smoothScrollToPosition(chatList.size -1)

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
    private fun getUserWorkerinfo(chatID: String) {
        val usersRaf = database.reference.child("workers").child(chatID)
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
            TextUtils.isEmpty(binding.idMessageText.text) -> Toast.makeText(this,"bir??eyler yaz",Toast.LENGTH_LONG).show()

            else->{

                val mesajText = binding.idMessageText.text.toString()
                binding.idMessageText.setText("")

                val mesajAtanMap = HashMap<String,Any>()
                mesajAtanMap["uid"] = auth.currentUser!!.uid
                mesajAtanMap["chat"] = mesajText
                mesajAtanMap["time"] = FieldValue.serverTimestamp().toString()
                mesajAtanMap["goruldu"] = true
                mesajAtanMap["type"] = "text"

                database.reference.child("Chats").child(auth.currentUser!!.uid).child(bosschatID).push().setValue(mesajAtanMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful){

                            val mesajAlanMap = HashMap<String,Any>()
                            mesajAlanMap["uid"] = auth.currentUser!!.uid
                            mesajAlanMap["chat"] = mesajText
                            mesajAlanMap["time"] = FieldValue.serverTimestamp().toString()
                            mesajAlanMap["goruldu"] = false
                            mesajAlanMap["type"] = "text"

                            database.reference.child("Chats").child(bosschatID).child(auth.currentUser!!.uid).push().setValue(mesajAlanMap)
                                .addOnCompleteListener {
                                    if (it.isSuccessful){

                                        val konusmaAtanMap = HashMap<String,Any>()
                                        konusmaAtanMap["time"] = FieldValue.serverTimestamp().toString()
                                        konusmaAtanMap["goruldu"] = true
                                        konusmaAtanMap["son_mesaj"] = mesajText

                                        database.reference.child("konusmalar").child(auth.currentUser!!.uid).child(bosschatID).setValue(konusmaAtanMap)
                                            .addOnCompleteListener {
                                                if(it.isSuccessful){

                                                    val konusmaAlanMap = HashMap<String,Any>()
                                                    konusmaAlanMap["time"] = FieldValue.serverTimestamp().toString()
                                                    konusmaAlanMap["goruldu"] = false
                                                    konusmaAlanMap["son_mesaj"] = mesajText

                                                    database.reference.child("konusmalar").child(bosschatID).child(auth.currentUser!!.uid).setValue(konusmaAlanMap)
                                                        .addOnCompleteListener {
                                                            if(it.isSuccessful){
                                                                chatActivityAdapter.notifyDataSetChanged()
                                                            }
                                                        }


                                                }
                                            }

                                    }
                                }
                        }
                    }


            }
        }

    }

    private fun CurrentBoosputChats(){

        when {
            TextUtils.isEmpty(binding.idMessageText.text) -> Toast.makeText(this,"bir??eyler yaz",Toast.LENGTH_LONG).show()

            else->{

                val message = binding.idMessageText.text.toString()
                val mesajText = message.toString().replace('\n', 's').trim()//binding.idMessageText.text.toString().replace()
                binding.idMessageText.setText("")

                val mesajAtanMap = HashMap<String,Any>()
                mesajAtanMap["uid"] = auth.currentUser!!.uid
                mesajAtanMap["chat"] = mesajText
                mesajAtanMap["time"] = FieldValue.serverTimestamp().toString()
                mesajAtanMap["goruldu"] = true
                mesajAtanMap["type"] = "text"

                database.reference.child("Chats").child(auth.currentUser!!.uid).child(workerchatID).push().setValue(mesajAtanMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful){

                            val mesajAlanMap = HashMap<String,Any>()
                            mesajAlanMap["uid"] = auth.currentUser!!.uid
                            mesajAlanMap["chat"] = mesajText
                            mesajAlanMap["time"] = FieldValue.serverTimestamp().toString()
                            mesajAlanMap["goruldu"] = false
                            mesajAlanMap["type"] = "text"

                            database.reference.child("Chats").child(workerchatID).child(auth.currentUser!!.uid).push().setValue(mesajAlanMap)
                                .addOnCompleteListener {
                                    if (it.isSuccessful){

                                        val konusmaAtanMap = HashMap<String,Any>()
                                        konusmaAtanMap["time"] = FieldValue.serverTimestamp().toString()
                                        konusmaAtanMap["goruldu"] = true
                                        konusmaAtanMap["son_mesaj"] = mesajText

                                        database.reference.child("konusmalar").child(auth.currentUser!!.uid).child(workerchatID).setValue(konusmaAtanMap)
                                            .addOnCompleteListener {
                                                if(it.isSuccessful){

                                                    val konusmaAlanMap = HashMap<String,Any>()
                                                    konusmaAlanMap["time"] = FieldValue.serverTimestamp().toString()
                                                    konusmaAlanMap["goruldu"] = false
                                                    konusmaAlanMap["son_mesaj"] = mesajText

                                                    database.reference.child("konusmalar").child(workerchatID).child(auth.currentUser!!.uid).setValue(konusmaAlanMap)
                                                        .addOnCompleteListener {
                                                            if(it.isSuccessful){
                                                                chatActivityAdapter.notifyDataSetChanged()
                                                            }
                                                        }


                                                }
                                            }

                                    }
                                }
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
                    for (id in (bossList as ArrayList<String>)){

                        if (auth.currentUser?.uid == id){
                            getBossChatMessage()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }

}
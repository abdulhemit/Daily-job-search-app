package com.example.gunlukis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.gunlukis.R
import com.example.gunlukis.databinding.ActivityMainBinding
import com.example.gunlukis.fragments.PostJobFragment
import com.example.gunlukis.fragments.homeFragment
import com.example.gunlukis.fragments.messageFragment
import com.example.gunlukis.fragments.profileFragment
import com.example.gunlukis.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
   private lateinit var binding : com.example.gunlukis.databinding.ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var workerList: MutableList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        WorkersUsers()

        var secilecekUserId = intent.extras?.get("secilenUserId")



        if(secilecekUserId != null){
            var secilenUserId = intent.extras?.getString("secilenUserId")
            var whichUser = intent.extras?.getString("whichUser")
            var click_action = intent.getStringExtra("click_action")
            var aktifUserId = intent.getStringExtra("aktifUserId")

            println("secilen User Id:" +secilenUserId)
            println(" aktif Kullanici idsi:" + auth.currentUser?.uid.toString())
            println("mesaj gonderen kisi:" +whichUser)
            println("click_action:" +click_action)



            val intent = Intent(this@MainActivity,ChatActivity::class.java)
            intent.putExtra("sohbetEdilecekUserId",secilenUserId.toString())
            intent.putExtra("aktifUserId",aktifUserId.toString())
            intent.putExtra("WhereIsFrom","FromMainActivity")
            startActivity(intent)
        }


    }
    



    private fun moveToFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment).commitAllowingStateLoss()
    }




    private val onNavigationItemSelectedListenerForWorkers = BottomNavigationView.OnNavigationItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.nav_homeWorker -> {
                        messageFragment.fragmentAcikMi = false
                        moveToFragment(homeFragment())
                        return@OnNavigationItemSelectedListener  true
                    }
                    R.id.nav_messageWorker -> {
                        messageFragment.fragmentAcikMi = true
                        moveToFragment(messageFragment())
                        /*val intent = Intent(this@MainActivity,test::class.java)
                        startActivity(intent)

                         */

                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.nav_profileWorker -> {
                        messageFragment.fragmentAcikMi = false
                        moveToFragment(profileFragment())
                        return@OnNavigationItemSelectedListener true
                    }

                }
        false
    }
    private val onNavigationItemSelectedListenerForBoss = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.nav_homeBoss -> {
                messageFragment.fragmentAcikMi = false
                moveToFragment(homeFragment())
                return@OnNavigationItemSelectedListener  true
            }
            R.id.nav_addJObBoss -> {
                messageFragment.fragmentAcikMi = false
                moveToFragment(PostJobFragment())
                return@OnNavigationItemSelectedListener  true

            }
            R.id.nav_messageBoss -> {
                messageFragment.fragmentAcikMi = true
                moveToFragment(messageFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profileBoss -> {
                messageFragment.fragmentAcikMi = false
                moveToFragment(profileFragment())
                return@OnNavigationItemSelectedListener true
            }

        }
        false
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

                    for(id in (workerList as ArrayList<String>)) {
                        val a = id

                        if (auth.currentUser!!.uid == id) {
                            binding.navViewForWorkers.visibility = View.VISIBLE
                            binding.navViewForBosses.visibility = View.GONE
                            binding.navViewForWorkers.setOnNavigationItemSelectedListener(onNavigationItemSelectedListenerForWorkers)

                                moveToFragment(homeFragment())


                        }else{
                            BossUsers()
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

        workerList = ArrayList()

        val workers = FirebaseDatabase.getInstance().reference
            .child("bosses")


        workers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (workerList as ArrayList<String>).add(it) }
                    }

                    for(id in (workerList as ArrayList<String>)) {

                        if (auth.currentUser!!.uid == id) {
                            binding.navViewForWorkers.visibility = View.GONE
                            binding.navViewForBosses.visibility = View.VISIBLE
                            binding.navViewForBosses.setOnNavigationItemSelectedListener(onNavigationItemSelectedListenerForBoss)

                            moveToFragment(homeFragment())
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
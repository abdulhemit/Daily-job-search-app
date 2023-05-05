package com.example.gunlukis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gunlukis.adapter.UserAdapter
import com.example.gunlukis.adapter.konusmalarAdapter
import com.example.gunlukis.databinding.FragmentMessageBinding
import com.example.gunlukis.models.User
import com.example.gunlukis.models.chat
import com.example.gunlukis.models.konusmalar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class messageFragment : Fragment() {
    private var _binding : FragmentMessageBinding? = null
    private val binding get() = _binding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var konusmalarList: MutableList<konusmalar>
    private lateinit var userList: MutableList<User>
    private lateinit var konusmalaradapter: konusmalarAdapter
    private lateinit var workerList: MutableList<User>
    private lateinit var bossList: MutableList<User>

    companion object {
        var fragmentAcikMi = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        konusmalarList = ArrayList()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val view = binding?.root
        WorkersUsers()
        BossUsers()
        userList = ArrayList()
        konusmalaradapter = konusmalarAdapter(konusmalarList)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding?.RecyclerviewFragmentMessage?.layoutManager = linearLayoutManager
        binding?.RecyclerviewFragmentMessage?.adapter = konusmalaradapter

        return binding?.root
    }

    override fun onStart() {
        super.onStart()
        binding?.RecyclerviewFragmentMessage?.recycledViewPool?.clear()
        konusmalaradapter.notifyDataSetChanged()
    }


    private fun konusmalariGetirmekWorker(){

        /*
        database.reference.child("konusmalar").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    konusmalarList.clear()
                    if(snapshot.exists()){
                        for (snap in snapshot.children){

                            var konusmalar = snap.getValue(konusmalar::class.java)
                            konusmalar?.userId = snap.key
                            konusmalar?.hangiKullanici = "worker"
                            konusmalar.let {
                                konusmalarList.add(konusmalar!!)

                            }
                        }
                        konusmalaradapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

         */

        konusmalarList.clear()
        database.reference.child("konusmalar").child(auth.currentUser!!.uid).orderByChild("time").addChildEventListener(object : ChildEventListener{


            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                var konusmalar = snapshot.getValue(konusmalar::class.java)
                konusmalar?.userId = snapshot.key
                konusmalar?.hangiKullanici = "worker"
                konusmalar.let {
                    konusmalarList.add(0,konusmalar!!)

                }
                konusmalaradapter.notifyItemInserted(0)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                // kontrol -1 ise yeni bir konusma eklenecek, kontrol -1 degil ise var olan konusmanin positini guncellenecek
                val kontrol = konusmaPositionBul(snapshot.key.toString())
                if ( kontrol != -1){

                    var guncellenecekKonusma = snapshot.getValue(konusmalar::class.java)
                    guncellenecekKonusma?.userId = snapshot.key
                    guncellenecekKonusma?.hangiKullanici = "worker"
                    guncellenecekKonusma.let {
                        konusmalarList.removeAt(kontrol)
                        konusmalaradapter.notifyItemRemoved(kontrol)
                        konusmalarList.add(0,guncellenecekKonusma!!)
                        konusmalaradapter.notifyItemInserted(0)

                    }

                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private fun konusmalariGetirmekBoss(){

        /*
        database.reference.child("konusmalar").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    konusmalarList.clear()
                    if(snapshot.exists()){
                        for (snap in snapshot.children){

                            var konusmalar = snap.getValue(konusmalar::class.java)
                            konusmalar?.userId = snap.key
                            konusmalar?.hangiKullanici = "boss"
                            konusmalar.let {
                                konusmalarList.add(konusmalar!!)

                            }
                        }
                        konusmalaradapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

         */

        konusmalarList.clear()
        database.reference.child("konusmalar").child(auth.currentUser!!.uid).orderByChild("time").addChildEventListener(object : ChildEventListener{


            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                var konusmalar = snapshot.getValue(konusmalar::class.java)
                konusmalar?.userId = snapshot.key
                konusmalar?.hangiKullanici = "boss"
                konusmalar.let {
                    konusmalarList.add(0,konusmalar!!)

                }
                konusmalaradapter.notifyItemInserted(0)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                // kontrol -1 ise yeni bir konusma eklenecek, kontrol -1 degil ise var olan konusmanin positini guncellenecek
                val kontrol = konusmaPositionBul(snapshot.key.toString())
                if ( kontrol != -1){

                    var guncellenecekKonusma = snapshot.getValue(konusmalar::class.java)
                    guncellenecekKonusma?.userId = snapshot.key
                    guncellenecekKonusma?.hangiKullanici = "boss"
                    guncellenecekKonusma.let {
                        konusmalarList.removeAt(kontrol)
                        konusmalaradapter.notifyItemRemoved(kontrol)
                        konusmalarList.add(0,guncellenecekKonusma!!)
                        konusmalaradapter.notifyItemInserted(0)

                    }

                }
/*
                var guncellenecekKonusma = snapshot.getValue(konusmalar::class.java)
                guncellenecekKonusma?.userId = snapshot.key
                guncellenecekKonusma?.hangiKullanici = "boss"
                guncellenecekKonusma.let {
                    konusmalarList.add(0,guncellenecekKonusma!!)

                }
                konusmalaradapter.notifyItemInserted(konusmalarList.size -1)
                konusmalaradapter.notifyDataSetChanged()

 */
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    private fun konusmaPositionBul(UserId : String): Int {

        for ( i in 0.. konusmalarList.size -1){

            var gecici = konusmalarList.get(i)

            if (gecici.userId!!.equals(UserId)){
                return i
            }

        }
        return - 1

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

                        if (auth.currentUser?.uid == id ){
                            konusmalariGetirmekWorker()
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

                        if (auth.currentUser?.uid == id ){
                            konusmalariGetirmekBoss()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }

    override fun onPause() {
        super.onPause()
        fragmentAcikMi = false
    }

    override fun onResume() {
        super.onResume()
        fragmentAcikMi = true

    }



}
package com.example.gunlukis.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gunlukis.activities.*
import com.example.gunlukis.databinding.FragmentProfileBinding
import com.example.gunlukis.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class profileFragment : Fragment() {

    // github token
    //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

    
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var workerList: MutableList<User>
    private lateinit var alertDialog : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.logOut.setOnClickListener {
            alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Dikkat !")
                .setMessage("çikmak istiyor musunuz ?")
                .setCancelable(true)
                .setPositiveButton("evet"){dialogInterface, it->
                    auth.signOut()
                    var intent = Intent(requireContext(), HangiKullaniciActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }.setNegativeButton("hayir"){dialogIterface,it->
                    dialogIterface.cancel()
                }
                .show()


        }


        WorkersUsers()

        binding.EditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        binding.bugunkuLanlarim.setOnClickListener {
            var intent = Intent(requireContext(),bugunkuilanlarActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)


        }
        binding.oncekiLanlarim.setOnClickListener {
            var intent = Intent(requireContext(),onceYayinlananilanlar::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



        return binding.root
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

                        if (auth.currentUser!!.uid == id) {
                            binding.constraintLayoutForBoss.visibility = View.GONE
                            binding.constraintLayoutForWorker.visibility = View.VISIBLE

                            getUserWorker()

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

    private fun getUserWorker() {

        val usersRaf = database.reference.child("workers").child(auth.currentUser!!.uid)

        usersRaf.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        binding.userName.text = it!!.userName
                        Picasso.get().load(it.image).into(binding.profileImage)
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

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
                            binding.constraintLayoutForWorker.visibility = View.GONE
                            binding.constraintLayoutForBoss.visibility = View.VISIBLE
                            getUserBoss()
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })
    }

    private fun getUserBoss() {

        val usersRaf = database.reference.child("bosses").child(auth.currentUser!!.uid)

        usersRaf.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        binding.userName.text = it!!.userName
                        Picasso.get().load(it.image).into(binding.profileImage)
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


}
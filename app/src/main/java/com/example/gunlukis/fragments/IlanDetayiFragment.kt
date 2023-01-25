package com.example.gunlukis.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.SharedElementCallback
import com.example.gunlukis.R
import com.example.gunlukis.activities.MainActivity
import com.example.gunlukis.databinding.FragmentHomeBinding
import com.example.gunlukis.databinding.FragmentIlanDetayiBinding
import com.example.gunlukis.models.PostJob
import com.example.gunlukis.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates


class IlanDetayiFragment : Fragment() {


    private var _binding : FragmentIlanDetayiBinding? = null
    private val binding get() = _binding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var postjobList: MutableList<User>
    private lateinit var bossList: MutableList<User>
    private lateinit var postId: String
    private lateinit var userId: String
    private var workerInfo by Delegates.notNull<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIlanDetayiBinding.inflate(inflater, container, false)
        val view = binding?.root
        val arg = arguments
        arg.let {
            postId = arg?.getString("postId").toString()
            userId = arg?.getString("userId").toString()
            workerInfo = arg?.getBoolean("workerInfo")!!

        }
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer,homeFragment())?.commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        binding?.ilaniSil?.setOnClickListener {
            deletePostJob()
            deletePostMYJob()
        }

        getBossId()
        getPostJobsIdLinst()


        return binding?.root
    }
    private fun getPostJobsIdLinst(){

        postjobList = ArrayList()
        val postRef = database.reference
            .child("PostJob")
            //.child(postId)




        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (postjobList as ArrayList<String>).add(it) }

                    }
                    postjobList

                    for(id in (postjobList as ArrayList<String>)) {

                        if (postId == id) {

                            for (userUid in (bossList as ArrayList<String>)){

                                if (userId == userUid){

                                    getPostJobs()
                                    getBossInfo()

                                }
                            }


                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun getPostJobs(){

        val postRef = database.reference
            .child("PostJob")
            .child(postId)


        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val postjob = snapshot.getValue<PostJob>(PostJob::class.java)
                    postjob.let {

                        binding?.isIlaniId?.setText(postjob?.ilanAdi)
                        binding?.isFiyatiId?.setText(postjob?.ilanFiyati)
                        binding?.isAciklamaId?.setText(postjob?.ilanAciklama)
                        binding?.isYeriId?.setText(postjob?.isAdresi)
                        println("isAdresi ${binding?.isYeriId?.setText(postjob?.isAdresi)}")
                        binding?.isSaati?.setText(postjob?.calismaSaati)

                        if (workerInfo == true){
                            binding?.CardviewYayin?.visibility = View.VISIBLE
                            binding?.CardviewSil?.visibility = View.GONE
                        }else{
                            binding?.CardviewYayin?.visibility = View.GONE
                            binding?.CardviewSil?.visibility = View.VISIBLE
                        }

                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun getBossId(){


            bossList = ArrayList()

            val boss = FirebaseDatabase.getInstance().reference
                .child("bosses")


            boss.addValueEventListener(object : ValueEventListener {
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
    private fun getBossInfo(){

        val boss = FirebaseDatabase.getInstance().reference
            .child("bosses")
            .child(userId)


        boss.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){


                        var user = snapshot.getValue(User::class.java)
                        user.let {
                            binding?.yayinciId?.setText(it?.userName)
                            Picasso.get().load(it?.image).into(binding?.ilanDetayiImage)


                        }



                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })

    }
    private fun deletePostJob(){

        val postRef = database.reference
            .child("PostJob")
            .child(postId)
            .removeValue()

            .addOnCompleteListener { task->
                if(task.isSuccessful){

                    Toast.makeText(requireContext(),"ilan silindi!",Toast.LENGTH_LONG).show()

                }
            }.addOnFailureListener { task->

            }

    }
    private fun deletePostMYJob(){
        val postRef = database.reference
            .child("myPostJob")
            .child(auth.currentUser!!.uid)
            .child(postId)
            .removeValue()

            .addOnCompleteListener { task->
                if(task.isSuccessful){

                    Toast.makeText(requireContext(),"ilan silindi!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(requireContext(), MainActivity::class.java))

                }
            }.addOnFailureListener { task->

            }

    }





}
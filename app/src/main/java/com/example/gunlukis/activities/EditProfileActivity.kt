package com.example.gunlukis.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gunlukis.databinding.ActivityEditProfileBinding
import com.example.gunlukis.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var workerList: MutableList<User>
    private lateinit var bossList: MutableList<User>
    private lateinit var storage: FirebaseStorage
    private var checker = ""
    private var clicked = ""
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? =  null
    var myUri = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = Firebase.storage
        registerLauncher()


        binding.saveInfoProfileEdit.setOnClickListener {
            if (checker == "clicked")
            {
                uploadImageAndUpdateInfo()
            }else
            {
                updateUserInfoOnly()
            }
        }

        binding.textviewChacgeImage.setOnClickListener {
            checker = "clicked"
            selectedimage(it)
        }


        WorkersUsers()
        binding.closeImageBtnProfileEdit.setOnClickListener {
            startActivity(Intent(this@EditProfileActivity, MainActivity::class.java))
            finish()
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

                    for(id in (workerList as ArrayList<String>)) {

                        if (auth.currentUser!!.uid == id) {
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
                        binding.idAdSoyAd.setText(it?.userName)
                        binding.idOnyazi.setText(it?.onYazi)
                        Picasso.get().load(it?.image).into(binding.imageProfileEditProfileActivity)

                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {

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

                    for(id in (bossList as ArrayList<String>)) {

                        if (auth.currentUser!!.uid == id) {
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
                        binding.idAdSoyAd.setText(it?.userName)
                        binding.idOnyazi.setText(it?.onYazi)
                        Picasso.get().load(it?.image).into(binding.imageProfileEditProfileActivity)
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    // kullanici bilgilerini guncellemek
    private fun updateUserInfoOnly() {
        when {
            TextUtils.isEmpty(binding.idAdSoyAd.text.toString()) -> {
                Toast.makeText(this, "Lütfen Adınızı ve SoyAdınızı yazın", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.idOnyazi.text.toString()) -> {
                Toast.makeText(this, "Lütfen bir önyazı ekleyin", Toast.LENGTH_LONG).show()
            }

            else -> {
                val progressDialog = ProgressDialog(this@EditProfileActivity)
                progressDialog.setTitle("Yükleniyor")
                progressDialog.setMessage("Lütfen biraz bekleyin...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                for(id in (workerList as ArrayList<String>)) {

                    if (auth.currentUser!!.uid == id) {
                        SaveWorkersUsersInfo(progressDialog)
                    }else{

                        for(id in (bossList as ArrayList<String>)) {

                            if (auth.currentUser!!.uid == id) {
                                SaveBossUsersInfo(progressDialog)
                            }
                        }

                    }
                }



            }
        }
    }

    private fun SaveWorkersUsersInfo(progressDialog: ProgressDialog) {

        val usersRaf = database.reference.child("workers")
        val userMap = HashMap<String,Any>()
        userMap["userName"] = binding.idAdSoyAd.text.toString()
        userMap["onYazi"] = binding.idOnyazi.text.toString()

        usersRaf.child(auth.currentUser!!.uid).updateChildren(userMap)

            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    progressDialog.dismiss()

                    val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else{
                    val message = task.exception.toString()
                    Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }

            }


    }
    private fun SaveBossUsersInfo(progressDialog: ProgressDialog){

        val userRef = database.reference.child("bosses")
        val userMap = HashMap<String,Any>()
        userMap["userName"] = binding.idAdSoyAd.text.toString()
        userMap["onYazi"] = binding.idOnyazi.text.toString()

        userRef.child(auth.currentUser!!.uid).updateChildren(userMap)

            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    startActivity(Intent(this@EditProfileActivity, MainActivity::class.java))
                    finish()

                }else{
                    progressDialog.dismiss()
                    val message = task.exception
                    Toast.makeText(this@EditProfileActivity,"$message",Toast.LENGTH_LONG).show()

                }
            }

    }
    private fun selectedimage(view : View){
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"izin gerekli ", Snackbar.LENGTH_INDEFINITE).setAction("izin verin"){
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            // permission granted
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == RESULT_OK){
                val intentForResult = result.data
                if (intentForResult != null){
                    selectedPicture = intentForResult.data
                    selectedPicture.let {
                        binding.imageProfileEditProfileActivity.setImageURI(selectedPicture)
                    }
                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }else{
                // permission denied
                Toast.makeText(this@EditProfileActivity,"izin gerekli!!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadImageAndUpdateInfo() {

        when {

            TextUtils.isEmpty(selectedPicture.toString()) -> {
                Toast.makeText(this,"Lütfen bir resim ekleyin", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.idAdSoyAd.text.toString()) -> {
                Toast.makeText(this, "Lütfen Adınızı ve SoyAdınızı yazın", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.idOnyazi.text.toString()) -> {
                Toast.makeText(this, "Lütfen bir önyazı ekleyin", Toast.LENGTH_LONG).show()
            }

            else ->{
                val progressDialog = ProgressDialog(this@EditProfileActivity)
                progressDialog.setTitle("Yükleniyor")
                progressDialog.setMessage("Lütfen biraz bekleyin...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val fileRef  = storage.reference.child("Profile pictures").child(auth.currentUser!!.uid + "jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(selectedPicture!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->

                    if (! task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }


                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri>{ task ->
                    if (task.isSuccessful){
                        val downloadUri = task.result
                        myUri = downloadUri.toString()

                        for(id in (workerList as ArrayList<String>)) {

                            if (auth.currentUser!!.uid == id) {
                                SaveWorkersUsersInfoAndImage(progressDialog)
                            }else{

                                for(id in (bossList as ArrayList<String>)) {

                                    if (auth.currentUser!!.uid == id) {
                                        SaveBossUsersInfoAndImage(progressDialog)
                                    }
                                }

                            }
                        }



                    }else{
                        progressDialog.dismiss()
                    }
                })

            }
        }

    }
    private fun SaveWorkersUsersInfoAndImage(progressDialog: ProgressDialog){

        val usersRaf = database.reference.child("workers")
        val userMap = HashMap<String,Any>()
        userMap["userName"] = binding.idAdSoyAd.text.toString()
        userMap["onYazi"] = binding.idOnyazi.text.toString()
        userMap["image"] = myUri

        usersRaf.child(auth.currentUser!!.uid).updateChildren(userMap)

            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    progressDialog.dismiss()

                    val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else{
                    val message = task.exception.toString()
                    Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }

            }

    }


    private fun SaveBossUsersInfoAndImage(progressDialog: ProgressDialog){

        val userRef = database.reference.child("bosses")
        val userMap = HashMap<String,Any>()
        userMap["userName"] = binding.idAdSoyAd.text.toString()
        userMap["onYazi"] = binding.idOnyazi.text.toString()
        userMap["image"] = myUri


        userRef.child(auth.currentUser!!.uid).updateChildren(userMap)

            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    startActivity(Intent(this@EditProfileActivity, MainActivity::class.java))
                    finish()

                }else{
                    progressDialog.dismiss()
                    val message = task.exception
                    Toast.makeText(this@EditProfileActivity,"$message",Toast.LENGTH_LONG).show()

                }
            }
    }

}
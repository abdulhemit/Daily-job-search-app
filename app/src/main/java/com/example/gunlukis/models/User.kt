package com.example.gunlukis.models

data class User(

    var uid: String? = null,
    var userName: String? = null,
    var eMail: String? = null,
    val onYazi: String? = null,
    var image: String? = null,

)


/*


    /*
    private fun getWorkersId(position: Int,context: Context){

        workerList = ArrayList()

        val boss = FirebaseDatabase.getInstance().reference
            .child("workers")


        boss.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        snap.key?.let { (workerList as ArrayList<String>).add(it) }

                    }

                    for(id in (workerList as ArrayList<String>)) {


                        if (postJob[position].uid != id) {
                            val a = postList.uid
                            id
                            val b= 1
                            goToIlanFagmenDetayi()

                        }

                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println( "hata" + error.message.toString())
            }

        })


    }
    private fun goToIlanFagmenDetayi(){
        val bundle = Bundle()
        val ilandetayiFragment = IlanDetayiFragment()
        bundle.putString(/* key = */ "postId",/* value = */
            postList.postId
        )
        bundle.putString(/* key = */ "userId",/* value = */
            postList.uid
        )
        ilandetayiFragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ilandetayiFragment).commit()
    }

     */
 */


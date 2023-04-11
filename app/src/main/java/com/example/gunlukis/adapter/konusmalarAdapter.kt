package com.example.gunlukis.adapter

import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gunlukis.R
import com.example.gunlukis.activities.ChatActivity
import com.example.gunlukis.databinding.KonusmalarListRowBinding
import com.example.gunlukis.models.User
import com.example.gunlukis.models.konusmalar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class konusmalarAdapter(var konusmalarList: List<konusmalar>):RecyclerView.Adapter<konusmalarAdapter.konusmalarHolder>() {

    class konusmalarHolder(var binding: KonusmalarListRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): konusmalarHolder {
        val binding = KonusmalarListRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return konusmalarHolder(binding)
    }

    override fun onBindViewHolder(holder: konusmalarHolder, position: Int) {


        if (konusmalarList.get(position).goruldu == false){

            holder.binding.gorulduBilgisi.visibility = View.VISIBLE
            var oankiKonusma = konusmalarList[position].son_mesaj?.toString()
            if (oankiKonusma.toString().length> 20 ){
                holder.binding.IdSonMesaj.setText(oankiKonusma.toString().trim().substring(0,20)+"...")
            }else {
                holder.binding.IdSonMesaj.setText(konusmalarList[position].son_mesaj)
            }
            holder.binding.IdSonMesaj.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.pickColor))



        }else {

            holder.binding.gorulduBilgisi.visibility = View.INVISIBLE

            konusmalarList[position].son_mesaj?.toString()?.length.let {
                var oankiKonusma = konusmalarList[position].son_mesaj?.toString()
                if (oankiKonusma.toString().length > 20 ){
                    holder.binding.IdSonMesaj.setText(oankiKonusma.toString().trim().substring(0,20)+"...")
                }else {
                    holder.binding.IdSonMesaj.setText(konusmalarList[position].son_mesaj)
                }
            }

            holder.binding.IdSonMesaj.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.gri))

        }



        holder.itemView.setOnClickListener {

            if(konusmalarList[position].hangiKullanici == "worker"){

                FirebaseDatabase.getInstance().reference
                    .child("konusmalar")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(konusmalarList[position].userId.toString())
                    .child("goruldu").setValue(true)
                    .addOnCompleteListener {

                        var intent = Intent(holder.itemView.context,ChatActivity::class.java)
                        intent.putExtra("chatID",konusmalarList[position].userId)
                        intent.putExtra("workerId",FirebaseAuth.getInstance().currentUser!!.uid)
                        holder.itemView.context.startActivity(intent)
                    }




            }
            if (konusmalarList[position].hangiKullanici == "boss"){

                FirebaseDatabase.getInstance().reference
                    .child("konusmalar")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(konusmalarList[position].userId!!)
                    .child("goruldu").setValue(true)
                    .addOnCompleteListener {

                        var intent = Intent(holder.itemView.context,ChatActivity::class.java)
                        intent.putExtra("workerId",konusmalarList[position].userId)
                        intent.putExtra("chatID",FirebaseAuth.getInstance().currentUser!!.uid)
                        holder.itemView.context.startActivity(intent)
                    }



            }

        }
        if (konusmalarList[position].hangiKullanici == "worker"){

            sohbetEdilenKullanicininBilgileriniGetirBoss(holder.binding.userName,holder.binding.userImage,konusmalarList[position].userId,holder.binding.IdSonMesaj)

        }else{
            sohbetEdilenKullanicininBilgileriniGetirWorker(holder.binding.userName,holder.binding.userImage,konusmalarList[position].userId,holder.binding.IdSonMesaj,holder.itemView.context,holder.binding.gorulduBilgisi)

        }
    }
    override fun getItemCount(): Int {
        return konusmalarList.size
    }


    private fun sohbetEdilenKullanicininBilgileriniGetirWorker(
        userName: TextView,
        userImage: CircleImageView,
        userId: String?,
        idSonMesaj: TextView,
        context: Context,
        gorulduBilgisi: ImageView
    ) {
        val usersRaf = FirebaseDatabase.getInstance().reference.child("workers").child(userId!!)
        usersRaf.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        userName.text= it?.userName
                        Picasso.get().load(it?.image).into(userImage)
                        //idSonMesaj.text = konusmalarLis
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }


        })
    }

    private fun sohbetEdilenKullanicininBilgileriniGetirBoss(
        userName: TextView,
        userImage: CircleImageView,
        userId: String?,
        idSonMesaj: TextView
    ) {
        val usersRaf = FirebaseDatabase.getInstance().reference.child("bosses").child(userId!!)
        usersRaf.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        userName.text= it?.userName
                        Picasso.get().load(it?.image).into(userImage)

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }


        })
    }





}
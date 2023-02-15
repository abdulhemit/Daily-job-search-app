package com.example.gunlukis.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gunlukis.activities.ChatActivity
import com.example.gunlukis.databinding.KonusmalarListRowBinding
import com.example.gunlukis.models.User
import com.example.gunlukis.models.konusmalar
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
        holder.binding.IdSonMesaj.setText(konusmalarList[position].son_mesaj)
        holder.itemView.setOnClickListener {
            if(konusmalarList[position].hangiKullanici == "worker"){

                var intent = Intent(holder.itemView.context,ChatActivity::class.java)
                intent.putExtra("chatID",konusmalarList[position].userId)
                holder.itemView.context.startActivity(intent)
            }

        }
        if (konusmalarList[position].hangiKullanici == "worker"){

            sohbetEdilenKullanicininBilgileriniGetirBoss(holder.binding.userName,holder.binding.userImage,konusmalarList[position].userId)

        }else{
            sohbetEdilenKullanicininBilgileriniGetirWorker(holder.binding.userName,holder.binding.userImage,konusmalarList[position].userId)

        }
    }

    override fun getItemCount(): Int {
        return konusmalarList.size
    }
    private fun sohbetEdilenKullanicininBilgileriniGetirBoss(
        userName: TextView,
        userImage: CircleImageView,
        userId: String?
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

    private fun sohbetEdilenKullanicininBilgileriniGetirWorker(
        userName: TextView,
        userImage: CircleImageView,
        userId: String?
    ) {
        val usersRaf = FirebaseDatabase.getInstance().reference.child("workers").child(userId!!)
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
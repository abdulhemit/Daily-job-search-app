package com.example.gunlukis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gunlukis.databinding.UserRowBinding
import com.example.gunlukis.models.User
import com.squareup.picasso.Picasso

class UserAdapter(var list: List<User>):RecyclerView.Adapter<UserAdapter.UserHolder>() {
    class UserHolder(var binding: UserRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = UserRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
       holder.binding.userName.text = list[position].userName
        Picasso.get().load(list[position].image).into(holder.binding.userImage)
    }

    override fun getItemCount(): Int {
      return list.size
    }
}
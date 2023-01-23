package com.example.gunlukis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gunlukis.databinding.ItemJobBinding
import com.example.gunlukis.models.PostJob

class JobAdapter(val postJob: List<PostJob>): RecyclerView.Adapter<JobAdapter.JobHolder>() {
    class JobHolder(val binding: ItemJobBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobHolder {
        var binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return JobHolder(binding)
    }

    override fun onBindViewHolder(holder: JobHolder, position: Int) {
       holder.binding.IsAdi.text = postJob[position].ilanAdi
       holder.binding.isFiyat.text = postJob[position].ilanFiyati
    }

    override fun getItemCount(): Int {
        return postJob.size
    }
}
package com.example.gunlukis.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gunlukis.databinding.ItemMyJobBinding
import com.example.gunlukis.models.PostJob



class MyJobAdapter(val postJob: List<PostJob>, val fragmentManager: FragmentManager): RecyclerView.Adapter<MyJobAdapter.MyJobHolder>() {

    class MyJobHolder(val binding: ItemMyJobBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: MyJobHolder, position: Int) {
        holder.binding.IsAdi.text = postJob[position].ilanAdi
        holder.binding.isFiyat.text = postJob[position].ilanFiyati
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobHolder {
        val binding =ItemMyJobBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyJobHolder(binding)
    }

    override fun getItemCount(): Int {
        return postJob.size
    }

}
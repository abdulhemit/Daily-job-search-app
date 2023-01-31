package com.example.gunlukis.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gunlukis.R
import com.example.gunlukis.databinding.ItemJobBinding
import com.example.gunlukis.fragments.IlanDetayiFragment
import com.example.gunlukis.models.PostJob

class JobAdapter(val postJob: List<PostJob>,val fragmentManager: FragmentManager): RecyclerView.Adapter<JobAdapter.JobHolder>() {
    class JobHolder(val binding: ItemJobBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return JobHolder(binding)
    }

    override fun onBindViewHolder(holder: JobHolder, position: Int) {

        holder.binding.IsAdi.text = postJob[position].ilanAdi
        holder.binding.isFiyat.text = postJob[position].ilanFiyati



        holder.itemView.setOnClickListener {

                val bundle = Bundle()
                val ilandetayiFragment = IlanDetayiFragment()
                bundle.putString(/* key = */ "postId",/* value = */
                    postJob[position].postId
                )
                bundle.putString(/* key = */ "userId",/* value = */
                    postJob[position].uid
                )
            postJob[position].worker?.let { info ->
                bundle.putBoolean(/* key = */ "workerInfo",/* value = */
                    info
                )
            }
                ilandetayiFragment.arguments = bundle
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ilandetayiFragment).commit()

        }

    }

    override fun getItemCount(): Int {
        return postJob.size
    }





}
package com.example.gunlukis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gunlukis.R
import com.example.gunlukis.models.chat
import com.google.firebase.auth.FirebaseAuth

class ChatActivityAdapter(var chatList :  List<chat>):RecyclerView.Adapter<ChatActivityAdapter.ChatHolder>() {

    val VIEW_TYPE_MESSAGE_SEND = 1
    val VIEW_TYPE_MESSAGE_RECEVIED = 2

     class ChatHolder( itemview : View):RecyclerView.ViewHolder(itemview){

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    private val diffutil = object : DiffUtil.ItemCallback<chat>(){
        override fun areItemsTheSame(oldItem: chat, newItem: chat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: chat, newItem: chat): Boolean {
            return oldItem == newItem
        }

    }
    private val recyclerListDiffer = AsyncListDiffer(this,diffutil)


     var chatList1 : List<chat>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {
        val chat = chatList[position]
        val id = chatList[position].uid
        if(chatList[position].uid == FirebaseAuth.getInstance().currentUser!!.uid){

            return VIEW_TYPE_MESSAGE_SEND

        }else{
            return VIEW_TYPE_MESSAGE_RECEVIED

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {

        if (viewType == VIEW_TYPE_MESSAGE_RECEVIED){

           val  view = LayoutInflater.from(parent.context).inflate(R.layout.chat_activity_row,parent,false)
        return ChatHolder(view)

        }else{

            var view = LayoutInflater.from(parent.context).inflate(R.layout.chat_activity_row_right,parent,false)
            return ChatHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {

        val userText = holder.itemView.findViewById<TextView>(R.id.chatText)
        val chatText = holder.itemView.findViewById<TextView>(R.id.chatTime)
        userText.setText(chatList.get(position).chat)


    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}
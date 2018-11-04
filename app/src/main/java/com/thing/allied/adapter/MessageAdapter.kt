package com.thing.allied.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thing.allied.FirestoreList
import com.thing.allied.R
import com.thing.allied.model.Message

class MessageAdapter(var context: Context, var messages: FirestoreList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageViewHolder {
        val rootView: View = LayoutInflater.from(context).inflate(R.layout.message_item, p0, false)
        return MessageViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(p0: MessageViewHolder, p1: Int) {
        val message = messages.keyAt(p1)

    }

    class MessageViewHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView) {
        }
    }
}
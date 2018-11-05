package com.thing.allied.adapter


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.thing.allied.R
import kotlinx.android.synthetic.main.attachment_item.view.*

class AttachmentAdapter(internal var context: Context?, private var attachments: List<String>) :
    RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.attachment_item, parent, false)
        return AttachmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        val fileUrl = attachments[position]
        holder.bind(fileUrl)
    }

    override fun getItemCount(): Int {
        return attachments.size
    }

    inner class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView as TextView

        fun bind(fileUrl: String) {
            //TODO Get filename from url
            textView.setOnClickListener {
                try {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(fileUrl)
                    context?.startActivity(i)
                } catch (anfe: ActivityNotFoundException) {
                    Log.e(TAG, anfe.message)
                }
            }
            val fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1)
            textView.text = fileName
        }

    }

    companion object {
        private const val TAG = "AttachmentAdapter"
    }
}
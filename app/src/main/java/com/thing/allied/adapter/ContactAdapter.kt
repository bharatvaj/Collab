package com.thing.allied.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.thing.allied.R
import java.util.ArrayList

import kotlinx.android.synthetic.main.contact_item.view.*

class ContactAdapter(private val context: Context, linkList: ArrayList<String>, private val isEditable: Boolean) :
    RecyclerView.Adapter<ContactAdapter.LinkViewHolder>() {
    private val linkList: List<String>

    init {
        this.linkList = linkList
        //        linkList.setOnModifiedListener(this);
        //        linkList.setOnRemoveListener(this);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
        return LinkViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = linkList[position]
        holder.linkUrl.text = link
//        holder.linkWebsite.text = ContactFactory.getWebsite(link)
        holder.linkImageView.setImageResource(ContactFactory.getIcon(link))
        holder.itemView.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        }
        //        if (isEditable) {
        //            holder.itemView.setOnLongClickListener(view -> {
        //                showAlertDialog(holder, link);
        //                return true;
        //            });
        //            if (linkPair.getValue().isEmpty()) {
        //                showAlertDialog(holder, link);
        //                holder.itemView.performLongClick();
        //            }
        //        }
    }

    override fun getItemCount(): Int {
        return linkList.size
    }
    //
    //    @Override
    //    public void onRemove(Link link) {
    //        notifyDataSetChanged();
    //    }
    //
    //    @Override
    //    public void onModified(Link link) {
    //        notifyDataSetChanged();
    //    }

    private object ContactFactory {

        internal fun getWebsite(url: String): String {
            var s = ""
            try {
                s = url.split("www.".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    .split(".com".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
            return s
        }

        internal fun getIcon(url: String): Int {
            return if (url.contains("git")) {
                R.drawable.github_icon
            } else if (url.contains("linkedin")) {
                R.drawable.linkedin_icon
            } else
                R.drawable.web_icon
        }

    }

    inner class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var linkImageView: ImageView = itemView.contactImageView!!
//        var linkWebsite: TextView = itemView.linkWebsite!!
        var linkUrl: TextView = itemView.contactUri!!
    }

    companion object {
        private val TAG = "ContactAdapter"
    }
}
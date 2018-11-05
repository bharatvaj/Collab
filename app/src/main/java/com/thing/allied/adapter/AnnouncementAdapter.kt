package com.thing.allied.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Picasso

import java.util.Date

import com.thing.allied.FirestoreList
import com.thing.allied.ProfileActivity
import com.thing.allied.R
import com.thing.allied.R.id.userAnnouncementLink
import com.thing.allied.handler.UserHandler
import com.thing.allied.model.Announcement

import kotlinx.android.synthetic.main.announcement_item.view.*
import kotlinx.android.synthetic.main.user_announcement_details.view.*

class AnnouncementAdapter(private var context: Context, private var announcements: FirestoreList<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.announcement_item, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements.keyAt(holder.adapterPosition) //FIXME?
        holder.bind(announcement)
    }

    override fun getItemCount(): Int {
        return announcements.size
    }

    inner class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var announcerImage = itemView.userImage!!
        var announcementHeader = itemView.announcementHeader!!
        var announcementMessage = itemView.announcementMessage!!
        var userAnnouncementLink = itemView.userAnnouncementLink!!
        var attachmentRecyclerView = itemView.attachmentRecyclerView!!
        var userName = itemView.userName

        fun bind(announcement: Announcement) {

            if (announcement.isImportant) {
                itemView.background = context.getDrawable(R.drawable.attachment_important_bg)
            }
            if (announcement.expiryDate != null && announcement.expiryDate!! > Date()) {
                itemView.alpha = 0.4f
            }
            announcementHeader.text = announcement.title
            announcementMessage.text = announcement.message

            if (announcement.firebaseUid == null) {
                announcerImage.setOnClickListener {
                    Toast.makeText(context, context.getString(R.string.user_anonymous), Toast.LENGTH_SHORT).show()
                }
            } else {
                UserHandler.getUser(announcement.firebaseUid!!, onUserUpdate = { user ->
                    if (user.displayImage.isEmpty()) {
                        Picasso.get().load(R.drawable.default_user).into(announcerImage)
                    } else {
                        Picasso.get().load(user.displayImage).error(R.drawable.default_user).into(announcerImage)
                    }
                    userName.text = user.name
                    userAnnouncementLink.setOnClickListener {
                        val intent = Intent(context, ProfileActivity::class.java)
                        intent.putExtra(context.getString(R.string.extra_profile_firebase_uid), user.firebaseUid)
                        context.startActivity(intent)
                    }
                })
            }
            val attachments = announcement.attachments
            attachmentRecyclerView.adapter = AttachmentAdapter(context, attachments)
            attachmentRecyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        }
    }
}

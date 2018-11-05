package com.thing.allied

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.thing.allied.adapter.AnnouncementAdapter
import com.thing.allied.adapter.AttachmentAdapter
import com.thing.allied.model.Announcement
import com.thing.allied.handler.UserHandler
import kotlinx.android.synthetic.main.announce_layout.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        setContentView(R.layout.activity_main)

        var announcements = FirestoreList<Announcement>(Announcement::class.java, UserHandler.announcementsRef)

        var adapter = AnnouncementAdapter(this, announcements)
        announcementRecyclerView.adapter = adapter
        announcementRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        announcements.setOnAddListener { _: String, _: Announcement ->
            adapter.notifyDataSetChanged()
            //endloading
            //mutex end
        }

        announcements.setOnDeleteListener { _: String, _: Announcement ->
            adapter.notifyDataSetChanged()
        }

        val attachments: ArrayList<String> = ArrayList()

        announceAttachmentRecyclerView!!.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val attachmentAdapter = AttachmentAdapter(this, attachments)
        announceAttachmentRecyclerView!!.adapter = attachmentAdapter

        attachmentAddButton.setOnClickListener { view ->
            val attachmentFile = attachmentAddEditText.text.toString()
            if (attachmentFile.isEmpty()) {
                Toast.makeText(this, getString(R.string.attachment_empty), Toast.LENGTH_LONG).show()
            }
            attachmentAddButton.setOnClickListener {
                attachments.add(attachmentFile)
                attachmentAddEditText!!.setText("")
                attachmentAdapter.notifyDataSetChanged()
            }
        }

        announceBtn.setOnClickListener {
            val announcement = Announcement(
                FirebaseAuth.getInstance().uid,
                announceTitle.text.toString(),
                annnounceMessage.text.toString(),
                attachments,
                Date(),
                null, //if (expiryCheckBox.isChecked) expiryDate else null,
                importantCheckBox.isChecked, ""
            )
            announcements.add(announcement)
        }
    }
}

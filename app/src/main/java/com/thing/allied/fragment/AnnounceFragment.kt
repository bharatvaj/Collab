package com.thing.allied.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import com.thing.allied.R
import com.thing.allied.adapter.AttachmentAdapter
import com.thing.allied.handler.UserHandler
import com.thing.allied.model.Announcement
import kotlinx.android.synthetic.main.fragment_announce.*
import java.util.*

class AnnounceFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_announce, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attachments: ArrayList<String> = ArrayList()
        announceAttachmentRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        val attachmentAdapter = AttachmentAdapter(context, attachments)
        announceAttachmentRecyclerView!!.adapter = attachmentAdapter


        attachmentAddButton.setOnClickListener {
            val attachmentFile = "https://www.google.com"
            if (attachmentFile.isEmpty()) {
                Toast.makeText(context, getString(R.string.attachment_empty), Toast.LENGTH_LONG).show()
            }
            attachmentAddButton.setOnClickListener {
                attachments.add(attachmentFile)
                attachmentAdapter.notifyDataSetChanged()
            }
        }

        announceBtn.setOnClickListener {
            val announcement = Announcement(
                FirebaseAuth.getInstance().uid,
                announceTitle.text.toString(),
                announceMessage.text.toString(),
                attachments,
                Date(),
                null, //if (expiryCheckBox.isChecked) expiryDate else null,
                false, ""
            )
            UserHandler.announcements.add(announcement)
            announceTitle.text.clear()
            announceMessage.text.clear()
            activity?.supportFragmentManager?.beginTransaction()
                ?.detach(this@AnnounceFragment)
                ?.attach(this@AnnounceFragment)
                ?.commit()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = AnnounceFragment()
    }
}

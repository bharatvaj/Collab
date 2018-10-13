package com.thing.collab.fragment

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth

import java.util.ArrayList
import java.util.Date

import com.thing.collab.FirestoreList
import com.thing.collab.R
import com.thing.collab.adapter.AttachmentAdapter
import com.thing.collab.handler.UserHandler
import com.thing.collab.model.Announcement
import kotlinx.android.synthetic.main.fragment_announce.*

class AnnounceFragment : BottomSheetDialogFragment() {

    val attachments: MutableList<String> = ArrayList()
    private lateinit var announcementFirestoreList: FirestoreList<Announcement>
//
//    private var onNotifyListener: (() -> Unit?)? = null
//
//    fun setOnNotifyListener(onNotifyListener: () -> Unit) {
//        this.onNotifyListener = onNotifyListener
//    }

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        announcementFirestoreList = FirestoreList(Announcement::class.java, UserHandler.getInstance().announcementsRef)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_announce, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expiryCheckBox.isChecked = true
        expiryCheckBox.setOnCheckedChangeListener { compoundButton, b -> expiryDatePicker.isEnabled = true }

        announceAttachmentRecyclerView!!.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val attachmentAdapter = AttachmentAdapter(context, attachments)
        announceAttachmentRecyclerView!!.adapter = attachmentAdapter

        attachmentAddButton.setOnClickListener { view ->
            val attachmentFile = attachmentAddEditText.text.toString()
            if (attachmentFile.isEmpty()) {
                Toast.makeText(
                    context,
                    "Attachment should not be empty, provide a valid url or browse a file",
                    Toast.LENGTH_LONG
                ).show()
            }
            attachmentAddButton.setOnClickListener {
                attachments.add(attachmentFile)
                attachmentAddEditText!!.setText("")
                attachmentAdapter.notifyDataSetChanged()
            }
        }

        expiryCheckBox.setOnCheckedChangeListener { _: View, b: Boolean ->
            expiryDatePicker.visibility = if (b) View.VISIBLE else View.GONE
        }

        val date = Date()
        var expiryDate = Date()
        expiryDatePicker.setFirstVisibleDate(date.year, date.month, date.date)

        expiryDatePicker.setOnDateSelectedListener { year, month, day, index ->
            expiryDate.year = year
            expiryDate.month = month
            expiryDate.date = day
        }

        announceBtn.setOnClickListener {


            val announcement = Announcement(
                FirebaseAuth.getInstance().uid,
                announceAnonymousPostCheckBox.isChecked,
                announceTitle.text.toString(),
                annnounceMessage.text.toString(),
                attachments,
                Date(),
                if (expiryCheckBox.isChecked) expiryDate else null,
                importantCheckBox.isChecked
            )
            announcementFirestoreList.add(announcement)
            dismiss()
//            onNotifyListener?.invoke()
        }
    }

    interface OnNotifyListener {
        fun onNotify()
    }

    companion object {

        fun newInstance(): AnnounceFragment {
            val args = Bundle()
            val fragment = AnnounceFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

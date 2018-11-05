package com.thing.allied

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.thing.allied.adapter.AnnouncementAdapter
import com.thing.allied.fragment.AnnounceFragment
import com.thing.allied.model.Announcement
import com.thing.allied.handler.UserHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        setContentView(R.layout.activity_main)

        var adapter = AnnouncementAdapter(this, UserHandler.announcements)
        announcementRecyclerView.adapter = adapter
        announcementRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        UserHandler.announcements.setOnAddListener { _: String, _: Announcement ->
            adapter.notifyDataSetChanged()
            //endloading
            //mutex end
        }

        UserHandler.announcements.setOnDeleteListener { _: String, _: Announcement ->
            adapter.notifyDataSetChanged()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.addFragmentContainer, AnnounceFragment.newInstance(), "tag_announce")
            .addToBackStack("back_stack_add_ann")
            .commit()
    }
}

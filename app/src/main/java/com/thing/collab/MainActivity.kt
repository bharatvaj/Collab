package com.thing.collab

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.thing.collab.adapter.AnnouncementAdapter
import com.thing.collab.fragment.AnnounceFragment
import com.thing.collab.model.Announcement
import com.thing.collab.handler.UserHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "MainActivity"

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.announceBtn -> {
                val fragment = AnnounceFragment.newInstance()
                fragment.show(supportFragmentManager, "announce_modal_dialog")
            } else -> Log.e(TAG, "View not handled")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        announceBtn.setOnClickListener(this)

        var announcements =
            FirestoreList<Announcement>(Announcement::class.java, UserHandler.getInstance().announcementsRef)
        var adapter = AnnouncementAdapter(this, announcements)

        announcementRecyclerView.adapter = adapter
        announcementRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        announcements.setOnAddListener { id: String, announcement: Announcement ->
            adapter.notifyDataSetChanged()
            //endloading
            //mutex end
        }

        announcements.setOnDeleteListener { id: String, announcement: Announcement ->
            adapter.notifyDataSetChanged()
        }
        announcements.populate(20)
    }
}

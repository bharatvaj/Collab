package com.thing.allied

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager

import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

import java.util.ArrayList

import com.thing.allied.adapter.ContactAdapter
import com.thing.allied.handler.UserHandler
import com.thing.allied.model.User

import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.contact_item.*
import kotlinx.android.synthetic.main.profile_header.*

class ProfileActivity : AppActivity() {
    private var contactAdapter: ContactAdapter? = null
    private var links: ArrayList<String> = ArrayList()

//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (user == null) {
//            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
//            if (data == null) {
//                //Display an error
//                Toast.makeText(this, "Image not picked", Toast.LENGTH_SHORT).show()
//                return
//            }
//            try {
//                val inputStream = contentResolver.openInputStream(data.data!!)
//                if (inputStream == null) {
//                    Toast.makeText(this, "Cannot read the provided image", Toast.LENGTH_SHORT).show()
//                    return
//                }
//                if (user == null) return
//
//                //TODO move FirebaseStorage code to UserHandler
//                //                FirebaseStorage.getInstance().getReference("images/").child(user.getFirebaseUid()).putStream(inputStream)
//                //                        .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
//                //                                .addOnSuccessListener(uri -> {
//                //                                    user.setDisplayImage(uri.toString());
//                //                                    UserHandler.getInstance().setUser(user, (user, flag) -> {
//                //                                        switch (flag) {
//                //                                            case UPDATED:
//                //                                                Glide.with(ProfileActivity.this).load(uri.toString()).into(profileImage);
//                //                                                Toast.makeText(ProfileActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
//                //                                                break;
//                //                                            case FAILED:
//                //                                                Toast.makeText(ProfileActivity.this, "Cannot update profile photo", Toast.LENGTH_SHORT).show();
//                //                                                break;
//                //                                        }
//                //                                    });
//                //                                }));
//            } catch (fnfe: FileNotFoundException) {
//                Log.e(TAG, fnfe.message)
//            }
//
//        }
//    }

    private fun showLoading(isLoading: Boolean) {
        profileProgressBar!!.isIndeterminate = isLoading
        profileProgressBar!!.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun initializeUI(user: User, firebaseUid: String, isEditable: Boolean) {
        showLoading(false)

        name.isEnabled = isEditable
        name.setText(if (user.name.isEmpty()) "No name" else user.name)
//        name.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (isEditable) android.R.drawable.ic_menu_edit else 0, 0)

        if (user.displayImage.isEmpty()) {
            Picasso.get().load(R.drawable.default_user).into(profileImage)
        } else {
            Picasso.get()
                .load(user.displayImage)
                .into(profileImage!!)
        }
        profileImage!!.isEnabled = isEditable

        profileLinkAdd!!.visibility = if (isEditable) View.VISIBLE else View.GONE

//        userSummaryEditText!!.visibility = if ((user.summary.isEmpty() || user.summary.isEmpty()) && isEditable)
//            View.VISIBLE
//        else
//            View.GONE
//        userSummaryEditText!!.setText(if (user.summary.isEmpty() || user.summary.isEmpty()) "" else user.summary)
//        userSummaryEditText!!.isEnabled = isEditable
//        userSummaryEditText!!.visibility = if (isEditable) View.VISIBLE else View.GONE
//        userSummaryEditText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0)

        profileLinkRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //        OnLoadListener<String> onLoadListener = () -> {
        //            if (contactAdapter != null) {
        //                contactAdapter.notifyDataSetChanged();
        //            }
        //        };

        contactAdapter = ContactAdapter(this, links, isEditable)
        profileLinkRecyclerView!!.adapter = contactAdapter


        useremail.visibility = if (isEditable) View.VISIBLE else View.GONE



        if (isEditable) {
            profileLinkAdd.setOnClickListener { this.add(user) }
            profileImage!!.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
            }
//            fab!!.setOnClickListener {
//                this.update(user)
//            }
            useremail!!.text = FirebaseAuth.getInstance().currentUser!!.email
            contactImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_plus, theme))
            contactUri.text = getString(R.string.profile_add_contact)
        }
    }


    fun add(user: User) {
        //TODO delegate operations to a fragment
        user.contacts.add("https://www.github.com/someone")
        contactAdapter!!.notifyDataSetChanged()
    }


    fun update(user: User) {
        showLoading(true)
        user.name = name.text.toString()
//        user.branch = profileBranch.text.toString()
//        if (userSummaryEditText.text != null) {
//            user.summary = userSummaryEditText.text.toString()
//        }
        try {
            UserHandler.setUser(user, onUserUpdate = {
                if (it == null) {
                    Toast.makeText(this, "User update failed", Toast.LENGTH_LONG).show()
                    showLoading(false)
                } else {
                    Toast.makeText(this, "User updated Successfully", Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var firebaseUid: String? = null
        firebaseUid = intent.extras!!.getString(getString(R.string.extra_profile_firebase_uid))
        if (firebaseUid.isNullOrEmpty()) {
            finish()
            return
        }
        setContentView(R.layout.activity_profile)

        val isEditable = firebaseUid == UserHandler.userUid

        showLoading(true)

        UserHandler.getUser(firebaseUid, onUserUpdate = {
            initializeUI(it, firebaseUid, isEditable)
        })
    }

    companion object {

        const val PICK_IMAGE = 1
        private const val TAG = "ProfileActivity"
    }
}
package com.thing.allied.handler


import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.*
import com.thing.allied.FirestoreList
import com.thing.allied.model.Announcement
import com.thing.allied.model.Gender
import com.thing.allied.model.User
import java.lang.reflect.InvocationTargetException
import java.util.*

/*
 * UserHandler does the following tasks asynchronously for updating the
 * cache of information to be updated as possible,
 * Updates the users cache every 45secs or when getUsers() is called explicitly
 *
 */
object UserHandler {
    /*
     * Cached users list for leaderboard purposes
     */
    private const val TAG = "UserHandler"

    private val usersRef = FirebaseFirestore.getInstance().collection("users")
    private val adminsRef = FirebaseFirestore.getInstance().collection("admins")
    private val announcementsRef = FirebaseFirestore.getInstance().collection("announcements")

    var announcements = FirestoreList<Announcement>(Announcement::class.java, announcementsRef)


    val isAdmin = false
    private val preferences: SharedPreferences? = null


    val userUid: String?
        get() = FirebaseAuth.getInstance().uid

    fun getUsers(onUserUpdate: (User?) -> Unit) {
        //TODO call method as soon as we get a user than fetching all users at a time
        usersRef.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val users = queryDocumentSnapshots.toObjects(User::class.java)
                for (user in users) {
                    onUserUpdate(user)
                }
            }
            .addOnFailureListener { onUserUpdate(null) }
    }

    /*
        /*
         * TODO save to device then upload to FirebaseStorage
         * Upload image with current firebase Uid
         * Returns the uploaded files reference string
         */
        *
        String uploadImage(ByteStreams byteStreams) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("images/");
            //add
            //////////comment out this section ////////
            imagesRef.child(getUser().getUserUid())
                    .putFile(null)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Log.i(TAG, "Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Success");
                        }
                    });
            ///////////////////////////////
            return "";
        }
        */
    fun updateUserFromAuth(firebaseUid: String, onUserUpdate: (User?) -> Unit) {
        usersRef.document(firebaseUid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                adminsRef.document(FirebaseAuth.getInstance().uid!!)
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            Log.e(TAG, "User already exist")
                            val user = documentSnapshot.toObject<User>(User::class.java)
                            onUserUpdate(user)
                        } else {
                            val user = User(
                                firebaseUid,
                                "",
                                "User",
                                0,
                                "",
                                "",
                                Gender.OTHER,
                                "",
                                ArrayList(),
                                ArrayList()
                            )
                            usersRef.document(FirebaseAuth.getInstance().currentUser!!.uid).set(user)
                                .addOnSuccessListener {
                                    onUserUpdate(user)
                                }.addOnFailureListener {
                                    onUserUpdate(null)
                                }
                        }
                    }
            }
            .addOnFailureListener {
                Log.e(TAG, "Cannot get user info")
                onUserUpdate(null)
            }
    }

    /*
        * Gets the user based on the specified userUid
        */
    fun getUser(firebaseUid: String, onUserUpdate: (User) -> Unit) {
        if (firebaseUid.isBlank()) {
            return
        }
        usersRef.document(firebaseUid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    try {
                        var user = documentSnapshot.toObject(User::class.java)
                        if (user != null) {
                            onUserUpdate(user)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Activity not found")
                    }
                }
            }
        //.addOnFailureListener { onUserUpdate(null) }
    }

    /*
        * Gets the user registered in firebase auth
        */
    fun getUser(onUserUpdate: (User?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        usersRef.document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    onUserUpdate(documentSnapshot.toObject<User>(User::class.java))
                } else {
                    onUserUpdate(null)
                }
            }
            .addOnFailureListener { onUserUpdate(null) }
    }

    /*
        * Sets the user registered in firebase auth
        */
    fun setUser(user: User?, onUserUpdate: (User?) -> Unit) {
        if (user == null) {
            onUserUpdate(null)
            return
        }
        if (user.firebaseUid.isEmpty()) {
            onUserUpdate(null)
            return
        }
        //if the user name changes in object update in FirebaseAuth
        val userUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(user.name)
            .build()
        FirebaseAuth.getInstance().currentUser!!.updateProfile(userUpdate)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "User name update failed")
                } else {
                    Log.i(TAG, "User name update success")
                }
            }
        usersRef.document(user.firebaseUid)
            .set(user, SetOptions.merge())
            .addOnSuccessListener { onUserUpdate(user) }
            .addOnFailureListener { onUserUpdate(user) }
    }


}

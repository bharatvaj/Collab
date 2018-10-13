package com.thing.collab.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.thing.collab.R;
import com.thing.collab.model.Gender;
import com.thing.collab.model.User;


import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/*
 * UserHandler does the following tasks asynchronously for updating the
 * cache of information to be updated as possible,
 * Updates the users cache every 45secs or when getUsers() is called explicitly
 *
 */
public class UserHandler {

    public static final int UPDATED = 0;
    public static final int FAILED = 1;

    private static final String TAG = "UserHandler";
    private static UserHandler instance;
    private static Context context;
    /*
     * Cached users list for leaderboard purposes
     */
    private CollectionReference usersRef = null;
    private DocumentReference userRef = null;
    //private CollectionReference userInboxCollection = null;
    //private CollectionReference userContactCollectionRef = null;
    //private CollectionReference userDetailCollectionRef = null;
    private CollectionReference userQuizCollectionRef = null;
    private CollectionReference announcementsRef = null;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private boolean isAdmin = false;
    private SharedPreferences preferences;

    private UserHandler() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "Fatal error");
            return;
        }

        preferences = context.getSharedPreferences(context.getString(R.string.settings_pref_file), MODE_PRIVATE);
        isAdmin = preferences.getBoolean(context.getString(R.string.settings_isAdmin), false);

        usersRef = db.collection("users");
        userRef = usersRef.document(FirebaseAuth.getInstance().getUid());
        announcementsRef = db.collection("announcements");
    }

    public static UserHandler getInstance(Context context) {
        UserHandler.context = context;
        return getInstance();
    }

    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();
        }
        return instance;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }



    public String getUserUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    public void getUsers(OnUpdateUserListener onUpdateUserListener) {
        //TODO call method as soon as we get a user than fetching all users at a time
        usersRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    for (User user : users) {
                        if (user == null) {
                            onUpdateUserListener.onUserUpdate(null, FAILED);
                        }
                        onUpdateUserListener.onUserUpdate(user, UPDATED);
                    }
                })
                .addOnFailureListener(e -> {
                    onUpdateUserListener.onUserUpdate(null, FAILED);
                });
    }

    /*
    /*
     * TODO save to device then upload to FirebaseStorage
     * Upload image with current firebase Uid
     * Returns the uploaded files reference string
     */
    /*
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
    public CollectionReference getAnnouncementsRef() {
        return announcementsRef;
    }

    public void updateUserFromAuth(@NonNull String firebaseUid, @NonNull OnUpdateUserListener onUpdateUserListener) {
        usersRef.document(firebaseUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    CollectionReference adminCollection = db.collection("admins");
                    adminCollection.document(FirebaseAuth.getInstance().getUid())
                            .get()
                            .addOnSuccessListener(docSnap -> {
                                isAdmin = docSnap.exists();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(context.getString(R.string.settings_isAdmin), isAdmin);
                                editor.commit();
                                if (documentSnapshot.exists()) {
                                    Log.e(TAG, "User already exist");
                                    User user = documentSnapshot.toObject(User.class);
                                    onUpdateUserListener.onUserUpdate(user, UPDATED);
                                } else {
                                    User user = new User(firebaseUid, "", auth.getCurrentUser().getDisplayName(), 0, "", "", Gender.OTHER, "", null);
                                    usersRef.document(firebaseUid)
                                            .set(user, SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                onUpdateUserListener.onUserUpdate(user, UPDATED);
                                            })
                                            .addOnFailureListener(e -> {
                                                onUpdateUserListener.onUserUpdate(null, FAILED);
                                            });
                                }
                            });

                })
                .addOnFailureListener(e ->
                {
                    Log.e(TAG, "Cannot get user info");
                    onUpdateUserListener.onUserUpdate(null, FAILED);
                });
    }

    /*
     * Gets the user based on the specified userUid
     */
    public void getUser(@NonNull String firebaseUid, @NonNull OnUpdateUserListener onUpdateUserListener) {
        if (firebaseUid == null || firebaseUid.isEmpty()) {
            onUpdateUserListener.onUserUpdate(null, FAILED);
            return;
        }
        usersRef.document(firebaseUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onUpdateUserListener.onUserUpdate(documentSnapshot.toObject(User.class), UPDATED);
                    }
                })
                .addOnFailureListener(e -> onUpdateUserListener.onUserUpdate(null, FAILED));
    }

    /*
     * Gets the user registered in firebase auth
     */
    public void getUser(@NonNull OnUpdateUserListener onUpdateUserListener) {
        usersRef
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onUpdateUserListener.onUserUpdate(documentSnapshot.toObject(User.class), UPDATED);
                    } else {
                        //if user object does'nt exist, create one
                        User user = new User(auth.getCurrentUser().getUid(), "", auth.getCurrentUser().getDisplayName(), 0, "", "", Gender.OTHER, "", null);
                        usersRef.document(auth.getCurrentUser().getUid())
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    onUpdateUserListener.onUserUpdate(user, UPDATED);
                                    Log.i(TAG, "Added new user to firestore");
                                })
                                .addOnFailureListener(e -> {
                                    onUpdateUserListener.onUserUpdate(user, FAILED);
                                    Log.i(TAG, "Adding registration failed, contact administrator if the problem persists");
                                });
                    }
                })
                .addOnFailureListener(e -> onUpdateUserListener.onUserUpdate(null, FAILED));
    }

    /*
     * Sets the user registered in firebase auth
     */
    public void setUser(User user, OnUpdateUserListener onUpdateUserListener) {
        CollectionReference usersCollectionRef = db.collection("users");
        if (user == null) {
            onUpdateUserListener.onUserUpdate(user, FAILED);
            return;
        }
        if (user.getFirebaseUid() == null) {
            onUpdateUserListener.onUserUpdate(user, FAILED);
            return;
        }
        //if the user name changes in object update in FirebaseAuth
        UserProfileChangeRequest userUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getName())
                .build();
        auth.getCurrentUser().updateProfile(userUpdate)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "User name update failed");
                    } else {
                        Log.i(TAG, "User name update success");
                    }
                });
        usersCollectionRef.document(user.getFirebaseUid())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(aVoid -> onUpdateUserListener.onUserUpdate(user, UPDATED))
                .addOnFailureListener(e -> onUpdateUserListener.onUserUpdate(user, FAILED));
    }

    public interface OnUpdateUserListener {
        void onUserUpdate(User user, int flags);
    }
}

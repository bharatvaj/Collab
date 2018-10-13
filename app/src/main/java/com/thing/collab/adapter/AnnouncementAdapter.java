package com.thing.collab.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.thing.collab.FirestoreList;
import com.thing.collab.R;
import com.thing.collab.model.Announcement;
import de.hdodenhof.circleimageview.CircleImageView;
import com.thing.collab.handler.UserHandler;

import static com.thing.collab.handler.UserHandler.UPDATED;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private static final String TAG = "AnnouncementAdapter";
    Context context;
    FirestoreList<Announcement> announcements;

    public AnnouncementAdapter(Context context, FirestoreList<Announcement> announcements) {
        this.context = context;
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.announcement_item, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = (Announcement)announcements.keySet().toArray()[position];//FIXME
        UserHandler.getInstance().getUser(announcement.getFirebaseUid(), (user, flag) -> {
            switch (flag) {
                case UPDATED:
                    if (user.getDisplayImage() == null) {
                        Glide.with(context).load(R.drawable.default_user).into(holder.announcerImage);
                    } else {
                        Glide.with(context).load(announcement.getAnonymousPost() || user.getDisplayImage().isEmpty() ?
                                R.drawable.default_user :
                                user.getDisplayImage()).into(holder.announcerImage);
                    }
                    holder.announcerImage.setOnClickListener((view -> {
                        if (announcement.getAnonymousPost()) {
                            Toast.makeText(context, "Posted Anonymously only admin users can view the profile", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Navigate to profile page", Toast.LENGTH_LONG).show();
                            /*TODO
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra(context.getString(R.string.extra_profile_firebase_uid), user.getFirebaseUid());
                            context.startActivity(intent);
                            */
                        }
                    }));
                    break;
            }
        });

        holder.announcementTitle.setText(announcement.getTitle());
        holder.announcementMessage.setText(announcement.getMessage());
        List<String> attachments = announcement.getAttachments();
        if (attachments == null) {
            holder.attachmentContainer.setVisibility(View.GONE);
        } else if (attachments.size() == 0) {
            holder.attachmentContainer.setVisibility(View.GONE);
        } else {
            holder.attachementRecyclerView.setAdapter(new AttachmentAdapter(context, attachments));
            holder.attachementRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.announcerImage)
        CircleImageView announcerImage;
        @BindView(R.id.announcementTitle)
        TextView announcementTitle;
        @BindView(R.id.announcementMessage)
        TextView announcementMessage;
        @BindView(R.id.attachmentContainer)
        LinearLayout attachmentContainer;
        @BindView(R.id.attachementRecyclerView)
        RecyclerView attachementRecyclerView;

        public AnnouncementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

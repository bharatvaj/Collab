package com.thing.collab.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.thing.collab.FirestoreList;
import com.thing.collab.R;
import com.thing.collab.adapter.AttachmentAdapter;
import com.thing.collab.handler.UserHandler;
import com.thing.collab.model.Announcement;

public class AnnounceFragment extends BottomSheetDialogFragment {
    @BindView(R.id.announceTitle)
    EditText announceTitle;
    @BindView(R.id.annnounceMessage)
    EditText annnounceMessage;
    @BindView(R.id.announceAttachmentRecyclerView)
    RecyclerView announceAttachmentRecyclerView;
    @BindView(R.id.dismiss)
    Button announceBtn;
    Unbinder unbinder;
    @BindView(R.id.announceAnonymousPostCheckBox)
    CheckBox announceAnonymousPostCheckBox;
    @BindView(R.id.attachmentAddButton)
    ImageButton attachmentAddButton;
    @BindView(R.id.attachmentAddEditText)
    EditText attachmentAddEditText;

    List<String> attachments = new ArrayList<>();

    Date expiryDate = new Date();
    @BindView(R.id.expiryDatePicker)
    DatePickerTimeline expiryDatePicker;
    @BindView(R.id.expiryCheckBox)
    CheckBox expiryCheckBox;
    FirestoreList<Announcement> announcementFirestoreList;

    private OnNotifyListener onNotifyListener;

    public void setOnNotifyListener(OnNotifyListener onNotifyListener){
        this.onNotifyListener = onNotifyListener;
    }

    public AnnounceFragment() {
    }

    public static AnnounceFragment newInstance() {
        Bundle args = new Bundle();
        AnnounceFragment fragment = new AnnounceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        announcementFirestoreList = new FirestoreList<>(Announcement.class, UserHandler.getInstance().getAnnouncementsRef());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_announce, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        expiryCheckBox.setChecked(true);
        expiryCheckBox.setOnCheckedChangeListener((compoundButton, b) -> expiryDatePicker.setEnabled(true));

        announceAttachmentRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        AttachmentAdapter attachmentAdapter = new AttachmentAdapter(rootView.getContext(), attachments);
        announceAttachmentRecyclerView.setAdapter(attachmentAdapter);

        attachmentAddButton.setOnClickListener(view -> {
            String attachmentFile = attachmentAddEditText.getText().toString();
            if (attachmentFile == null || attachmentFile.isEmpty()) {
                Toast.makeText(rootView.getContext(), "Attachment should be empty, provide a valid url or browse a file", Toast.LENGTH_LONG).show();
                return;
            }
            attachments.add(attachmentFile);
            attachmentAddEditText.setText("");
            attachmentAdapter.notifyDataSetChanged();
        });

        Date date = new Date();
        expiryDatePicker.setFirstVisibleDate(date.getYear(), date.getMonth(), date.getDate());

        expiryDatePicker.setOnDateSelectedListener((year, month, day, index) -> {
            expiryDate.setYear(year);
            expiryDate.setMonth(month);
            expiryDate.setDate(day);
        });

        announceBtn.setOnClickListener(v -> {

            if (!expiryCheckBox.isChecked()) {
                expiryDate = null;
            }

            Announcement announcement = new Announcement(FirebaseAuth.getInstance().getUid(), announceAnonymousPostCheckBox.isChecked(), announceTitle.getText().toString(), annnounceMessage.getText().toString(), attachments, new Date(), expiryDate);
            announcementFirestoreList.add(announcement);
            AnnounceFragment.this.dismiss();
            onNotifyListener.onNotify();
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface OnNotifyListener {
        void onNotify();
    }
}

package com.example.bankassistadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aniketjain.weatherapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private EnquiryAdapter adapter;
    private List<VideoEnquiry> enquiryList;
    private ProgressBar progressBar;
    private TextView tvNoData, head;
    private String userDealWith;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoData = view.findViewById(R.id.tvNoData);

        head=view.findViewById(R.id.tvDeal);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enquiryList = new ArrayList<>();
        adapter = new EnquiryAdapter(getContext(), enquiryList);
        recyclerView.setAdapter(adapter);

        fetchUserDealWith();


        return view;
    }


    private void fetchUserDealWith() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Admin").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child("dealWith").getValue() != null) {
                    userDealWith = snapshot.child("dealWith").getValue(String.class);
                    head.setText(userDealWith);
                    fetchEnquiries();
                } else {
                    progressBar.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText("No assigned category.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchEnquiries() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Enquiries");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                enquiryList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot enquirySnapshot : userSnapshot.getChildren()) {
                        VideoEnquiry enquiry = enquirySnapshot.getValue(VideoEnquiry.class);
                        if (enquiry != null && enquiry.getIdentifiedIntent().equals(userDealWith)) {
                            enquiryList.add(enquiry);
                        }
                    }
                }

                if (enquiryList.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText("No enquiries found for your category.");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }




}

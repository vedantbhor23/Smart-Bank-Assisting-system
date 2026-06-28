package com.example.bankassist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aniketjain.weatherapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class EnquiriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private VideoEnquiryAdapter adapter;
    private List<VideoEnquiry> enquiryList;
    private ProgressBar progressBar;
    private TextView tvNoData;

    public EnquiriesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enquiry, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoData = view.findViewById(R.id.tvNoData);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enquiryList = new ArrayList<>();
        adapter = new VideoEnquiryAdapter(getContext(), enquiryList, this::openDetailFragment);
        recyclerView.setAdapter(adapter);

        fetchData();

        return view;

    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Enquiries").child(uid);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                enquiryList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        VideoEnquiry enquiry = dataSnapshot.getValue(VideoEnquiry.class);
                        enquiryList.add(enquiry);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
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

    private void openDetailFragment(VideoEnquiry enquiry) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("enquiryData", enquiry);  // ✅ Use Parcelable instead of Serializable
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }



}
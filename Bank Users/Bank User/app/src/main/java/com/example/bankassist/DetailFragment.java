package com.example.bankassist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aniketjain.weatherapp.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class DetailFragment extends Fragment {

    private PlayerView playerView;
    private TextView tvTranscription, tvIntent, tvStatus, tvPublishDate;
    private Button btnDelete;
    private ExoPlayer player;
    private VideoEnquiry enquiry;
    private DatabaseReference databaseReference;

    public DetailFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        playerView = view.findViewById(R.id.playerView);
        tvTranscription = view.findViewById(R.id.tvTranscription);
        tvIntent = view.findViewById(R.id.tvIntent);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPublishDate = view.findViewById(R.id.tvPublishDate);
        btnDelete = view.findViewById(R.id.btnDelete);

        if (getArguments() != null) {
            enquiry = getArguments().getParcelable("enquiryData");  // ✅ Use Parcelable
            if (enquiry != null) {
                displayDetails();
            }
        }

        btnDelete.setOnClickListener(v -> deleteEnquiry());

        return view;
    }

    private void displayDetails() {
        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(enquiry.getVideoUrl());
        player.setMediaItem(mediaItem);
        player.prepare();

        tvTranscription.setText(enquiry.getTranscriptionText());
        tvIntent.setText("Intent: " + enquiry.getIdentifiedIntent());
        tvStatus.setText("Status: " + enquiry.getStatus());
        tvPublishDate.setText("Published on: " + new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(enquiry.getTimestamp()));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Enquiries").child(uid).child(String.valueOf(enquiry.getTimestamp()));
    }

    private void deleteEnquiry() {
        databaseReference.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to delete!", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.release();
        }
    }

}
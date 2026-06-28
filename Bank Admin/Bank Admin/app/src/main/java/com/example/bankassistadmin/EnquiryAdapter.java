package com.example.bankassistadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aniketjain.weatherapp.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.ViewHolder> {
    private Context context;
    private List<VideoEnquiry> enquiryList;

    public EnquiryAdapter(Context context, List<VideoEnquiry> enquiryList) {
        this.context = context;
        this.enquiryList = enquiryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_enquiry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoEnquiry enquiry = enquiryList.get(position);

        // ✅ Null Check for UID Before Using It
        if (enquiry.getUid() == null || enquiry.getUid().isEmpty()) {
            Toast.makeText(context, "Error: UID is missing for this enquiry!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set ExoPlayer for Video
        ExoPlayer player = new ExoPlayer.Builder(context).build();
        holder.videoPlayer.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(enquiry.getVideoUrl());
        player.setMediaItem(mediaItem);
        player.prepare();

        holder.tvName.setText("Name: " + enquiry.getName());
        holder.tvEmail.setText("Email: " + enquiry.getEmail());
        holder.tvPhone.setText("Phone: " + enquiry.getMobile());
        holder.tvStatus.setText("Status: " + enquiry.getStatus());
        holder.tvDate.setText("Applied on: " + new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(enquiry.getTimestamp()));


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Enquiries")
                .child(enquiry.getUid())
                .child(String.valueOf(enquiry.getTimestamp()));

        // Approve Button
        holder.btnApprove.setOnClickListener(v -> {
            ref.child("status").setValue("Accept").addOnSuccessListener(aVoid ->
                    Toast.makeText(context, "Status updated to Accept", Toast.LENGTH_SHORT).show()
            );
        });

        // Reject Button
        holder.btnReject.setOnClickListener(v -> {
            ref.child("status").setValue("Rejected").addOnSuccessListener(aVoid ->
                    Toast.makeText(context, "Status updated to Rejected", Toast.LENGTH_SHORT).show()
            );
        });

        // Delete Button
        holder.btnDelete.setOnClickListener(v -> {
            ref.removeValue().addOnSuccessListener(aVoid ->
                    Toast.makeText(context, "Enquiry Deleted", Toast.LENGTH_SHORT).show()
            );
        });
    }

    @Override
    public int getItemCount() {
        return enquiryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PlayerView videoPlayer;
        TextView tvName, tvEmail, tvPhone, tvStatus, tvDate;
        Button btnApprove, btnReject, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoPlayer = itemView.findViewById(R.id.videoPlayer);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

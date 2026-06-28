package com.example.bankassist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aniketjain.weatherapp.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VideoEnquiryAdapter extends RecyclerView.Adapter<VideoEnquiryAdapter.ViewHolder> {
    private Context context;
    private List<VideoEnquiry> enquiryList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(VideoEnquiry enquiry);
    }

    public VideoEnquiryAdapter(Context context, List<VideoEnquiry> enquiryList, OnItemClickListener listener) {
        this.context = context;
        this.enquiryList = enquiryList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_enquiry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoEnquiry enquiry = enquiryList.get(position);
        holder.tvIntent.setText(enquiry.getIdentifiedIntent());
        holder.tvStatus.setText("Status: " + enquiry.getStatus());
        holder.tvPublishDate.setText("Date: " + new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(enquiry.getTimestamp()));

        // Initialize ExoPlayer
        ExoPlayer player = new ExoPlayer.Builder(context).build();
        holder.videoPlayer.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(enquiry.getVideoUrl());
        player.setMediaItem(mediaItem);
        player.prepare();

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(enquiry));
    }

    @Override
    public int getItemCount() { return enquiryList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PlayerView videoPlayer;
        TextView tvIntent, tvStatus, tvPublishDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoPlayer = itemView.findViewById(R.id.videoPlayer);
            tvIntent = itemView.findViewById(R.id.tvIntent);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPublishDate = itemView.findViewById(R.id.tvPublishDate);
        }
    }
}

package com.example.bankassist;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoEnquiry implements Parcelable {
    private String videoUrl;
    private String transcriptionText;
    private String identifiedIntent;
    private long timestamp;
    private String status;

    public VideoEnquiry() {
    }

    public VideoEnquiry(String videoUrl, String transcriptionText, String identifiedIntent, long timestamp, String status) {
        this.videoUrl = videoUrl;
        this.transcriptionText = transcriptionText;
        this.identifiedIntent = identifiedIntent;
        this.timestamp = timestamp;
        this.status = status;
    }

    protected VideoEnquiry(Parcel in) {
        videoUrl = in.readString();
        transcriptionText = in.readString();
        identifiedIntent = in.readString();
        timestamp = in.readLong();
        status = in.readString();
    }

    public static final Creator<VideoEnquiry> CREATOR = new Creator<VideoEnquiry>() {
        @Override
        public VideoEnquiry createFromParcel(Parcel in) {
            return new VideoEnquiry(in);
        }

        @Override
        public VideoEnquiry[] newArray(int size) {
            return new VideoEnquiry[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoUrl);
        dest.writeString(transcriptionText);
        dest.writeString(identifiedIntent);
        dest.writeLong(timestamp);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getVideoUrl() { return videoUrl; }
    public String getTranscriptionText() { return transcriptionText; }
    public String getIdentifiedIntent() { return identifiedIntent; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}


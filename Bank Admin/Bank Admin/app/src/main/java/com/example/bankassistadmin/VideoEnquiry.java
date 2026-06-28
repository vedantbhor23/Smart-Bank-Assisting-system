package com.example.bankassistadmin;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoEnquiry implements Parcelable {
    private String uid;  // ✅ Add UID field
    private String videoUrl;
    private String name;
    private String email;
    private String mobile;
    private String identifiedIntent;
    private long timestamp;
    private String status;

    public VideoEnquiry() {
    }

    public VideoEnquiry(String uid, String videoUrl, String name, String email, String mobile, String identifiedIntent, long timestamp, String status) {
        this.uid = uid;
        this.videoUrl = videoUrl;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.identifiedIntent = identifiedIntent;
        this.timestamp = timestamp;
        this.status = status;
    }

    protected VideoEnquiry(Parcel in) {
        uid = in.readString();
        videoUrl = in.readString();
        name = in.readString();
        email = in.readString();
        mobile = in.readString();
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
        dest.writeString(uid);
        dest.writeString(videoUrl);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(identifiedIntent);
        dest.writeLong(timestamp);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // ✅ Getters & Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mno) { this.mobile = mno; }

    public String getIdentifiedIntent() { return identifiedIntent; }
    public void setIdentifiedIntent(String identifiedIntent) { this.identifiedIntent = identifiedIntent; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

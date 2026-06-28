package com.example.bankassist;

public class VideoTranscription {
    private String videoUrl;
    private String transcriptionText;
    private String identifiedIntent;
    private long timestamp;
    private String status;
    private String name;
    private String email;
    private String mobile;

    private String uid;

    // Default constructor (Required for Firebase)
    public VideoTranscription() {
    }

    public VideoTranscription(String videoUrl, String transcriptionText, String identifiedIntent, long timestamp,
                              String status, String name, String email, String mobile, String uid) {
        this.videoUrl = videoUrl;
        this.transcriptionText = transcriptionText;
        this.identifiedIntent = identifiedIntent;
        this.timestamp = timestamp;
        this.status = status;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.uid = uid;
    }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getTranscriptionText() { return transcriptionText; }
    public void setTranscriptionText(String transcriptionText) { this.transcriptionText = transcriptionText; }

    public String getIdentifiedIntent() { return identifiedIntent; }
    public void setIdentifiedIntent(String identifiedIntent) { this.identifiedIntent = identifiedIntent; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}





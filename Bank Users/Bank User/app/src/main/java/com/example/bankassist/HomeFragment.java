package com.example.bankassist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.aniketjain.weatherapp.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {


    private static final int PICK_VIDEO_REQUEST = 1;
    private static final String API_KEY_ID = "r39u5waRbl3nhUc8";
    private static final String API_KEY_SECRET = "YxJuzS0rsW2xZ7yU";
    private static final String UPLOAD_URL = "https://api.speechflow.io/asr/file/v1/create";
    private static final String QUERY_URL = "https://api.speechflow.io/asr/file/v1/query?taskId=";

    private PlayerView playerView;
    private ExoPlayer player;
    private Button btnSelectVideo, btnUploadVideo, btnIdentifyIntent, btnSave;
    private TextView tvTranscription, tvIdentifiedIntent;
    private Uri selectedVideoUri;
    private String uploadedVideoUrl, finalTranscription, identifiedIntent;
    private ProgressDialog progressDialog;

    private StorageReference secondaryStorageRef;
    private DatabaseReference primaryDatabaseRef,secondaryDatabaseRef;
    private Handler handler = new Handler();
    private int attemptCount = 0;
    private final int MAX_ATTEMPTS = 10;

    CardView cvidentifyintent;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        playerView = view.findViewById(R.id.playerView);
        btnSelectVideo = view.findViewById(R.id.btn_select_video);
        btnUploadVideo = view.findViewById(R.id.btn_upload_video);
        btnIdentifyIntent = view.findViewById(R.id.btn_identify_intent);
        btnSave = view.findViewById(R.id.btn_save);
        tvTranscription = view.findViewById(R.id.tv_transcription);
        tvIdentifiedIntent = view.findViewById(R.id.tv_identified_intent);

        cvidentifyintent = view.findViewById(R.id.cv_identified_intent);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        secondaryStorageRef = FirebaseStorage.getInstance("gs://missing-person-4f9a0.appspot.com").getReference("videos");
        primaryDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        secondaryDatabaseRef = FirebaseDatabase.getInstance().getReference("Enquiries");

        btnSelectVideo.setOnClickListener(v -> selectVideo());
        btnUploadVideo.setOnClickListener(v -> uploadVideo());
        btnIdentifyIntent.setOnClickListener(v -> identifyIntent());
        btnSave.setOnClickListener(v -> saveToFirebase());



        return view;
    }

    private void identifyIntent() {
        cvidentifyintent.setVisibility(View.VISIBLE);

        if (finalTranscription == null || finalTranscription.isEmpty()) {
            tvIdentifiedIntent.setText("No transcription available.");
            return;
        }

        String transcriptLower = finalTranscription.toLowerCase();

        if (transcriptLower.contains("new loan") || transcriptLower.contains("apply loan") ||
                transcriptLower.contains("loan application") || transcriptLower.contains("loan request") ||
                transcriptLower.contains("need a loan")) {
            identifiedIntent = "New Loan Request";
        }
        else if (transcriptLower.contains("loan help") || transcriptLower.contains("loan support") ||
                transcriptLower.contains("loan issue") || transcriptLower.contains("loan customer service")) {
            identifiedIntent = "Loan Assistance";
        }
        else if (transcriptLower.contains("loan repayment") || transcriptLower.contains("due loan") ||
                transcriptLower.contains("pay loan emi") || transcriptLower.contains("loan overdue") ||
                transcriptLower.contains("loan billing")) {
            identifiedIntent = "Loan Repayment";
        }
        else if (transcriptLower.contains("fraud transaction") || transcriptLower.contains("unauthorized charge") ||
                transcriptLower.contains("dispute charge") || transcriptLower.contains("stolen card")) {
            identifiedIntent = "Fraud or Dispute";
        }
        else {
            identifiedIntent = "General Banking Query";
        }

        tvIdentifiedIntent.setText("Identified Intent: " + identifiedIntent);
        tvIdentifiedIntent.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
    }



    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            initializeExoPlayer(selectedVideoUri);
            playerView.setVisibility(View.VISIBLE);
            btnUploadVideo.setVisibility(View.VISIBLE);
        }
    }

    private void initializeExoPlayer(Uri videoUri) {
        if (player != null) {
            player.release();
        }
        player = new ExoPlayer.Builder(getContext()).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void uploadVideo() {
        if (selectedVideoUri == null) return;

        progressDialog.setMessage("Uploading video...");
        progressDialog.show();

        StorageReference videoRef = secondaryStorageRef.child("video_" + System.currentTimeMillis() + ".mp4");
        videoRef.putFile(selectedVideoUri)
                .addOnProgressListener(taskSnapshot -> progressDialog.setMessage("Uploading... " +
                        (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) + "%"))
                .addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    uploadedVideoUrl = uri.toString();
                    progressDialog.setMessage("Video uploaded! Transcribing...");
                    transcribeVideo();
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void transcribeVideo() {
        tvTranscription.setText("Processing video...");

        RequestBody formBody = new FormBody.Builder()
                .add("lang", "en")
                .add("remotePath", uploadedVideoUrl)
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .addHeader("keyId", API_KEY_ID)
                .addHeader("keySecret", API_KEY_SECRET)
                .post(formBody)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> tvTranscription.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    if (jsonObject.getInt("code") == 10000) {
                        pollTranscription(jsonObject.getString("taskId"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void pollTranscription(String taskId) {
        if (attemptCount >= MAX_ATTEMPTS) {
            progressDialog.dismiss();
            getActivity().runOnUiThread(() -> tvTranscription.setText("Transcription timed out."));
            return;
        }

        Request request = new Request.Builder()
                .url(QUERY_URL + taskId + "&resultType=4")
                .addHeader("keyId", API_KEY_ID)
                .addHeader("keySecret", API_KEY_SECRET)
                .get()
                .build();


        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    progressDialog.dismiss();
                    tvTranscription.setText("Error: " + e.getMessage());
                    Toast.makeText(getContext(), "Request Failed!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    int code = jsonObject.getInt("code");

                    if (code == 11000) {
                        finalTranscription = jsonObject.optString("result", "No transcription available.");
                        getActivity().runOnUiThread(() -> {
                            progressDialog.dismiss();
                            tvTranscription.setText(finalTranscription);
                            btnIdentifyIntent.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.VISIBLE);
                        });
                    } else if (code == 11001) {
                        // Transcription still in progress, retry after 3 seconds
                        handler.postDelayed(() -> pollTranscription(taskId), 3000);
                    } else {
                        getActivity().runOnUiThread(() -> {
                            progressDialog.dismiss();
                            try {
                                tvTranscription.setText("Error: " + jsonObject.getString("msg"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        tvTranscription.setText("Error parsing transcription response.");
                    });
                }
            }
        });


    }

    private void saveToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        long timestamp = new Date().getTime();
        DatabaseReference userRef = primaryDatabaseRef.child(uid);
        DatabaseReference enquiryref = secondaryDatabaseRef.child(uid);

        // Fetch User Details (Name, Email, Mobile)
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobile = snapshot.child("mno").getValue(String.class);

                    // Create the transcription data object
                    VideoTranscription transcriptionData = new VideoTranscription(
                            uploadedVideoUrl, finalTranscription, identifiedIntent, timestamp, "Pending", name, email, mobile, uid);

                    // Store under UID → Timestamp
                    DatabaseReference transcriptionRef = enquiryref.child(String.valueOf(timestamp));
                    transcriptionRef.setValue(transcriptionData)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.setMessage("Video Saved Successfully!");
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Saved Successfully!", Toast.LENGTH_SHORT).show();

                                // ✅ CLEAR TRANSCRIPTION TEXT
                                tvTranscription.setText("");

                                // ✅ HIDE "Identify Intent" BUTTON & TEXTVIEW
                                btnIdentifyIntent.setVisibility(View.GONE);
                                tvIdentifiedIntent.setVisibility(View.GONE);
                                cvidentifyintent.setVisibility(View.GONE);
                                tvIdentifiedIntent.setText("");

                                // ✅ HIDE "Save to Firebase" BUTTON
                                btnSave.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Failed to save data!", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to fetch user details!", Toast.LENGTH_SHORT).show();
            }
        });
    }






}
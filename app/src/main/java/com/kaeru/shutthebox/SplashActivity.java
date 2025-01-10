package com.kaeru.shutthebox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        VideoView videoView = findViewById(R.id.videoView);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.kaeru);
        videoView.setVideoURI(videoUri);
        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            if (mAuth.getCurrentUser() != null) {
                // User is logged in, check if the username is set
                String userId = mAuth.getCurrentUser().getUid();
                checkUserUsername(userId);
            } else {
                // No user logged in, navigate to LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkUserUsername(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if the username field exists
                            if (document.contains("username") && document.getString("username") != null) {
                                // Username is set, navigate to MainActivity
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Username is not set, navigate to LoginActivity
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // User does not exist in Firestore, navigate to LoginActivity
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Error while fetching user data, navigate to LoginActivity
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}

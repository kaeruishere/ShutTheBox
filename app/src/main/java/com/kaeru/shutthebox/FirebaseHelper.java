package com.kaeru.shutthebox;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private final FirebaseFirestore db;

    public FirebaseHelper(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    // Zar sonuçlarını kaydet
    public void addDiceNumber(int diceNumber, FirebaseCallback callback) {
        db.collection("diceNumbers")
                .add(new Dice(diceNumber))
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Dice number added: " + diceNumber);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding dice number", e);
                    callback.onFailure(e);
                });
    }

    // Skoru kaydet
    public void addScore(int score, FirebaseCallback callback) {
        db.collection("scores")
                .add(new Score(score))
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Score added: " + score);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding score", e);
                    callback.onFailure(e);
                });
    }

    // Callback interface
    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Veri sınıfları
    public static class Dice {
        public int number;

        public Dice() {} // Firestore için boş constructor
        public Dice(int number) {
            this.number = number;
        }
    }

    public static class Score {
        public int score;

        public Score() {} // Firestore için boş constructor
        public Score(int score) {
            this.score = score;
        }
    }
}

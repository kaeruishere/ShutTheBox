package com.kaeru.shutthebox.ui.dashboard;

public class Score {
    private int score;

    public Score() {
        // Firestore için gerekli boş constructor
    }

    public Score(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

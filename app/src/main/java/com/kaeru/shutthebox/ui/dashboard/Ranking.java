package com.kaeru.shutthebox.ui.dashboard;
public class Ranking {
    private String userId;
    private String userName;
    private int totalScore;

    public Ranking(String userId, String userName, int totalScore, double ratio) {
        this.userId = userId;
        this.userName = userName;
        this.totalScore = totalScore;
    }

    // Getter ve Setter metodları
    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getTotalScore() {
        return totalScore;
    }
}

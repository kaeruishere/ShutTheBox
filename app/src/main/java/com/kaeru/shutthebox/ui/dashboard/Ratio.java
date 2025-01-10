package com.kaeru.shutthebox.ui.dashboard;
public class Ratio {
    private String userId;
    private String userName;
    private String ratio;

    public Ratio(String userId, String userName, String ratio) {
        this.userId = userId;
        this.userName = userName;
        this.ratio = ratio;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getRatio() {
        return ratio;
    }
}

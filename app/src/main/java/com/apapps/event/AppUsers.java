package com.apapps.event;

/**
 * Created by Aditi on 4/22/2017.
 */

public class AppUsers {
    private String uId;
    private String uName;

    AppUsers() {
    }

    public AppUsers(String uId, String uName) {
        this.uId = uId;
        this.uName = uName;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}

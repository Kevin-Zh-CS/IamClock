package com.example.iamclockapp.pojo;

import java.util.Date;

public class User {
    private String userName;
    private String encreptPassword;
    private Date getupDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEncreptPassword() {
        return encreptPassword;
    }

    public void setEncreptPassword(String encreptPassword) {
        this.encreptPassword = encreptPassword;
    }

    public Date getGetupDate() {
        return getupDate;
    }

    public void setGetupDate(Date getupDate) {
        this.getupDate = getupDate;
    }
}

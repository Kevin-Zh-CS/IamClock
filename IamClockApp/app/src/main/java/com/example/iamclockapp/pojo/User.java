package com.example.iamclockapp.pojo;

import java.util.Date;

public class User {
    private String userName;
    private String encryptPassword;
    private Date getupDate;
    private boolean isLogin;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword;
    }

    public Date getGetupDate() {
        return getupDate;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public void setGetupDate(Date getupDate) {
        this.getupDate = getupDate;
    }
}

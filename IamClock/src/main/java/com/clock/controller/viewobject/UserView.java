package com.clock.controller.viewobject;


import java.util.Date;

public class UserView {
    private String userName;
    private Date getupDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getGetupDate() {
        return getupDate;
    }

    public void setGetupDate(Date getupDate) {
        this.getupDate = getupDate;
    }
}

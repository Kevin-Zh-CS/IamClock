package com.clock.service.model;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class UserModel {
    private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String userName;
    private Date getupDate;
    @NotBlank(message = "密码不能为空")
    private String encrptPassword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }
}

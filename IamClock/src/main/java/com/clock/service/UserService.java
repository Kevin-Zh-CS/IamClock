package com.clock.service;

import com.clock.error.BusinessException;
import com.clock.service.model.UserModel;

import java.util.Date;
import java.util.List;

public interface UserService {
    UserModel getUserById(Integer id);
    void updateUserByName(String username, Date time);
    void register(UserModel userModel) throws BusinessException;
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;
    List<UserModel> getAllUser();
}

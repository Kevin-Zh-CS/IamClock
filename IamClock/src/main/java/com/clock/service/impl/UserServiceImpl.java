package com.clock.service.impl;

import com.clock.dao.InfoDataObjMapper;
import com.clock.dao.PasswordDataObjMapper;
import com.clock.dao.dataobject.InfoDataObj;
import com.clock.dao.dataobject.PasswordDataObj;
import com.clock.error.BusinessError;
import com.clock.error.BusinessException;
import com.clock.service.UserService;
import com.clock.service.model.UserModel;
import com.clock.validator.ValidationResult;
import com.clock.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private InfoDataObjMapper infoDataObjMapper;

    @Autowired
    private PasswordDataObjMapper passwordDataObjMapper;

    @Autowired
    private ValidatorImpl validator;


    @Override
    public UserModel getUserById(Integer id) {
        InfoDataObj infoDataObj = infoDataObjMapper.selectByPrimaryKey(id);
        PasswordDataObj passwordDataObj = passwordDataObjMapper.selectByUserId(id);
        if (infoDataObj == null) {
            return null;
        }
        return convertFromObjectToModel(infoDataObj, passwordDataObj);
    }

    @Override
    public void updateUserByName(String username, Date time){
        InfoDataObj infoDataObj = infoDataObjMapper.selectByUserName(username);
        infoDataObj.setGetupDate(time);
        infoDataObjMapper.updateByPrimaryKey(infoDataObj);
    }


    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR);
        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, result.getErrorMsg());
        }
        InfoDataObj infoDataObj = convertFromModelToInfoDataObj(userModel);
        try {
            infoDataObjMapper.insertSelective(infoDataObj);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(BusinessError.USER_DUPLICATE_REGISTER);
        }
        userModel.setId(infoDataObj.getId());
        PasswordDataObj passwordDataObj = convertFromModelToPasswordDataObj(userModel);
        passwordDataObjMapper.insertSelective(passwordDataObj);
    }

    @Override
    public UserModel validateLogin(String username, String encrptPassword) throws BusinessException {
        InfoDataObj infoDataObj = infoDataObjMapper.selectByUserName(username);
        if (infoDataObj == null) {
            throw new BusinessException(BusinessError.USER_LOGIN_FAIL);
        }
        PasswordDataObj passwordDataObj = passwordDataObjMapper.selectByUserId(infoDataObj.getId());
        UserModel userModel = convertFromObjectToModel(infoDataObj, passwordDataObj);
        //比对密码
        if (!StringUtils.equals(encrptPassword, passwordDataObj.getEncrptPassword())) {
            throw new BusinessException(BusinessError.USER_LOGIN_FAIL);
        }
        return userModel;

    }

    @Override
    public List<UserModel> getAllUser() {
        List<InfoDataObj> infoDataObjList = infoDataObjMapper.getAllUser();
        return infoDataObjList.stream().map(infoDataObj -> {
            PasswordDataObj passwordDataObj = passwordDataObjMapper.selectByUserId(infoDataObj.getId());
            return convertFromObjectToModel(infoDataObj, passwordDataObj);
        }).collect(Collectors.toList());
    }


    private UserModel convertFromObjectToModel(InfoDataObj infoDataObj, PasswordDataObj passwordDataObj) {
        if (infoDataObj == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(infoDataObj, userModel);
        if (passwordDataObj != null) {
            userModel.setEncrptPassword(passwordDataObj.getEncrptPassword());
        }
        return userModel;
    }

    private InfoDataObj convertFromModelToInfoDataObj(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        InfoDataObj infoDataObj = new InfoDataObj();
        BeanUtils.copyProperties(userModel, infoDataObj);
        return infoDataObj;
    }

    private PasswordDataObj convertFromModelToPasswordDataObj(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        PasswordDataObj passwordDataObj = new PasswordDataObj();
        passwordDataObj.setEncrptPassword(userModel.getEncrptPassword());
        passwordDataObj.setUserId(userModel.getId());
        return passwordDataObj;
    }

}

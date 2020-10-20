package com.clock.controller;

import com.clock.controller.viewobject.UserView;
import com.clock.dao.dataobject.InfoDataObj;
import com.clock.error.BusinessError;
import com.clock.error.BusinessException;
import com.clock.response.CommonReturnType;
import com.clock.service.UserService;
import com.clock.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*"}, allowCredentials = "true")
@Controller("user")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
//    @Autowired
//    private HttpServletRequest httpServletRequest;


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //如果获取的用户对象不存在
        if (userModel == null) {
            throw new BusinessException(BusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserView userView = convertFromModelToView(userModel);

        //返回通用对象
        return CommonReturnType.create(userView);
    }

    @RequestMapping("/list")
    @ResponseBody
    public CommonReturnType getAllUser() {
        List<UserModel> userModelList = userService.getAllUser();
        List<UserView> userViewList = userModelList.stream().map(this::convertFromModelToView).collect(Collectors.toList());
        return CommonReturnType.create(userViewList);
    }

    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "username") String username,
                                     @RequestParam(name = "password") String password) throws NoSuchAlgorithmException, BusinessException {
        UserModel userModel = new UserModel();
        userModel.setUserName(username);
        userModel.setEncrptPassword(encodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "username") String username,
                                  @RequestParam(name = "password") String password) throws BusinessException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR);
        }
        UserModel userModel = userService.validateLogin(username, encodeByMd5(password));
//        HttpSession session = httpServletRequest.getSession();
//        session.setAttribute("IS_LOGIN", true);
//        session.setAttribute("LOGIN_USER", userModel);
//        System.out.println(userModel.getUserName());
        //作为请求头使用
        return CommonReturnType.create(userModel.getUserName());
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType update(@RequestParam(name = "header") String header,
                                   @RequestParam(name = "time")Date time){

        userService.updateUserByName(header, time);
        return CommonReturnType.create(null);
    }

    //signout前端完成
//    @RequestMapping(value = "/signout", method = {RequestMethod.POST})
//    @ResponseBody
//    public CommonReturnType signOut(String str) throws BusinessException {
//        HttpSession session = httpServletRequest.getSession();
//        Boolean isLogin = (Boolean) session.getAttribute("IS_LOGIN");
//        if(isLogin == null || !isLogin){
//            throw new BusinessException(BusinessError.USER_NOT_LOGIN);
//        }
//        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
//        System.out.println(userModel.getUserName());
//        return CommonReturnType.create(null);
//    }

    private UserView convertFromModelToView(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserView userView = new UserView();
        BeanUtils.copyProperties(userModel, userView);
        return userView;
    }

    public String encodeByMd5(String str) throws NoSuchAlgorithmException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder encoder = Base64.getEncoder();
        //加密字符串
        return encoder.encodeToString(md5.digest(str.getBytes(StandardCharsets.UTF_8)));
    }
}

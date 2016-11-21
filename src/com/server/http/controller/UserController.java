package com.server.http.controller;

import com.server.common.enums.ErrorCodeEnum;
import com.server.common.sessionManager.SessionManager;
import com.server.http.dao.i.UserDao;
import com.server.http.entity.UserEntity;
import com.server.http.service.i.UserService;
import com.server.common.util.DataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;

/**
 * Created by Administrator on 2016/6/22.
 */
@Controller
@RequestMapping(value="/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;


    //登录
    @RequestMapping(value="/login")
    @ResponseBody
    public DataWrapper<UserEntity> login(
            @RequestParam(value = "username",required = true) String username,
            @RequestParam(value = "password",required = true) String password){

        return userService.login(username,password);
    }
    //获取验证码
    @RequestMapping(value="/getVerifyCode")
    @ResponseBody
    public DataWrapper<Void> getVerifyCode(
            @RequestParam(value = "phone",required = true) String phone){
        return userService.getVerifyCode(phone);
    }
    //注册
    @RequestMapping(value="/register")
    @ResponseBody
    public DataWrapper<UserEntity> registerUser(
            @RequestParam(value = "username",required = true) String username,
            @RequestParam(value = "password",required = true) String password,
            @RequestParam(value = "code",required = true) String code){
        return userService.register(username,password,code);
    }
    //修改密码
    @RequestMapping(value="changePWD")
    @ResponseBody
    public DataWrapper<Void> changePWD(
            @RequestParam(value = "oldPWD",required = true) String oldPWD,
            @RequestParam(value = "newPWD",required = true) String newPWD,
            @RequestParam(value = "token",required = true) String token){
        return userService.changePWD(oldPWD,newPWD,token);
    }
    //忘记密码
    @RequestMapping(value="forgetPWD")
    @ResponseBody
    public DataWrapper<Void> forgetPWD(
            @RequestParam(value = "username",required = true) String username,
            @RequestParam(value = "password",required = true) String password,
            @RequestParam(value = "code",required = true) String code){
        return userService.forgetPWD(username,password,code);
    }
    //完善用户信息
    @RequestMapping(value="addUserInfo")
    @ResponseBody
    public DataWrapper<UserEntity>  addUserInfo(
            @RequestParam(value = "realName") String realName,
            @RequestParam(value = "pic") String pic,
            @RequestParam(value = "token") String token){
        UserEntity user= SessionManager.getSession(token);
        user.setRealname(realName);
        user.setPic(pic);

        return userService.updateUser(user);
    }
    //通过userName查找用户
    @RequestMapping(value="findUser")
    @ResponseBody
    public DataWrapper<UserEntity> findUser(
            @RequestParam(value = "userName") String userName){
        DataWrapper<UserEntity> ret=new DataWrapper<>();
        UserEntity user= userDao.getUserByUsername(userName);
        if(user==null){
            ret.setErrorCode(ErrorCodeEnum.Username_NOT_Exist);
            return ret;
        }
        ret.setErrorCode(ErrorCodeEnum.No_Error);
        ret.setData(user);
        return ret;
    }
    //获取好友列表
    @RequestMapping(value="getFriendList")
    @ResponseBody
    public DataWrapper<List<String>>  getFriendList(
            @RequestParam(value = "token") String token){
        DataWrapper<List<String>> ret=new DataWrapper<>();
        UserEntity user= SessionManager.getSession(token);
        String friends=user.getFriends();
        List<String> friendList= Arrays.asList(friends.split("\\;"));
        ret.setData(friendList);
        ret.setErrorCode(ErrorCodeEnum.No_Error);

        return ret;
    }
    //添加好友
    @RequestMapping(value="addFriend")
    @ResponseBody
    public DataWrapper<Void>  addFriend(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "friend") String friend){
        UserEntity user= SessionManager.getSession(token);
        String friends=user.getFriends();
        friends=friends+friend+";";

        return userService.addFriends(user.getId(),friends);
    }





    //获取用户列表
//    @RequestMapping(value="getUserList")
//    @ResponseBody
//    public DataWrapper<List<UserEntity>> getUserList(
//            @RequestParam(value = "token",required = false) String token){
//        return userService.getUserList();
//    }
    @RequestMapping(value="addUser", method = RequestMethod.POST)
    @ResponseBody
    public DataWrapper<Void> addUser(
            @ModelAttribute UserEntity user,
            @RequestParam(value = "token",required = false) String token){
        return userService.addUser(user);
    }
//    @RequestMapping(value="deleteUser")
//    @ResponseBody
//    public DataWrapper<Void> deleteUser(
//            @RequestParam(value = "id",required = false) Long id,
//            @RequestParam(value = "token",required = false) String token){
//        return userService.deleteUser(id);
//    }

    @RequestMapping(value="updateUser",method = RequestMethod.POST)
    @ResponseBody
    public DataWrapper<UserEntity> updateUser(
            @ModelAttribute UserEntity user,
            @RequestParam(value = "token",required = false) String token){
        System.out.println(user);
        return userService.updateUser(user);
    }
}

package com.server.controller;




import com.server.user.UserEntity;
import com.server.user.UserService;
import com.server.util.DataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2016/6/22.
 */
@Controller
@RequestMapping(value="api/user")
public class UserController {
    @Autowired
    UserService userService;
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
    public DataWrapper<Void> updateUser(
            @ModelAttribute UserEntity user,
            @RequestParam(value = "token",required = false) String token){
        System.out.println(user);
        return userService.updateUser(user);
    }
    //登录
    @RequestMapping(value="login")
    @ResponseBody
    public DataWrapper<UserEntity> login(
            @RequestParam(value = "username",required = true) String username,
            @RequestParam(value = "password",required = true) String password){

        return userService.login(username,password);
    }
    //获取注册验证码
    @RequestMapping(value="getVerifyCode")
    @ResponseBody
    public DataWrapper<Void> getVerifyCode(
            @RequestParam(value = "phone",required = true) String phone){


        return userService.getVerifyCode(phone);
    }
    //注册
    @RequestMapping(value="register")
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
    //获取用户列表
//    @RequestMapping(value="getUserList")
//    @ResponseBody
//    public DataWrapper<List<UserEntity>> getUserList(
//            @RequestParam(value = "token",required = false) String token){
//        return userService.getUserList();
//    }
}

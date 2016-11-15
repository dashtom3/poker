package com.server.http.service.impl;


import com.server.common.enums.ErrorCodeEnum;
import com.server.http.dao.i.UserDao;
import com.server.http.entity.UserEntity;
import com.server.http.service.i.UserService;
import com.server.common.sessionManager.SessionManager;
import com.server.common.sessionManager.VerifyCodeManager;
import com.server.common.util.DataWrapper;
import com.server.common.util.HttpUtil;
import com.server.common.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Override
    public DataWrapper<Void> addUser(UserEntity user) {
        DataWrapper<Void> dataWrapper = new DataWrapper<Void>();

        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> deleteUser(int id) {
        DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
        if(!userDao.deleteUser(id)) {
            dataWrapper.setErrorCode(ErrorCodeEnum.Error);
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> updateUser(UserEntity user) {
        DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
        if(!userDao.updateUser(user)) {
            dataWrapper.setErrorCode(ErrorCodeEnum.Error);
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<UserEntity> login(String username,String password) {
        DataWrapper<UserEntity> dataWrapper = new DataWrapper<UserEntity>();
        UserEntity user = userDao.getUserByUsername(username);
        String token=null;
        if(user != null) {
            if (user.getPassword().equals(MD5Util.getMD5Password(password))) {
                if(user.getType()>0){
                    //游戏通讯服务的session管理
                    token=SessionManager.newSession(user);
                }else{
                    //TODO 管理员操作
                }
                dataWrapper.setData(user);
                dataWrapper.setToken(token);
                return dataWrapper;
            }
            dataWrapper.setErrorCode(ErrorCodeEnum.Login_Error);
        }
        dataWrapper.setErrorCode(ErrorCodeEnum.Username_NOT_Exist);
        return dataWrapper;
    }

    @Override
    public DataWrapper<UserEntity> register(String username, String password, String code) {
        DataWrapper<UserEntity> dataWrapper = new DataWrapper<UserEntity>();
        UserEntity user = userDao.getUserByUsername(username);
        if(user == null){
            //验证码服务
            String serverCode=VerifyCodeManager.getPhoneCode(username);
            if(serverCode.equals("noCode")){
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_notExist);
            }
            else if(serverCode.equals("overdue")){
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_5min);
            }
            else if(serverCode.equals(code)){
                UserEntity newUser = new UserEntity();
                newUser.setUsername(username);
                newUser.setPassword(MD5Util.getMD5Password(password));
                newUser.setType(1);
                if (userDao.register(newUser)) {
                    //移除验证码
                    VerifyCodeManager.removePhoneCodeByPhoneNum(username);
                    dataWrapper.setData(newUser);
                    return dataWrapper;
                } else {
                    dataWrapper.setErrorCode(ErrorCodeEnum.Register_Error);
                }
            }
            else {
                System.out.println("code:"+code);
                System.out.println("VerifyCode:"+VerifyCodeManager.getPhoneCode(username));
                dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_Error);
            }
        }else {
            dataWrapper.setErrorCode(ErrorCodeEnum.Username_Already_Exist);
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> getVerifyCode(String phoneNum) {
        //五分钟之内不能再发短信
        DataWrapper<Void> dataWrapper = new DataWrapper<>();
        String code = VerifyCodeManager.newPhoneCode(phoneNum);
        if(code == null){
            dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_5min);
            return dataWrapper;
        }
        HttpUtil httpUtil = new HttpUtil();
        boolean result = httpUtil.sendPhoneVerifyCode(code,phoneNum);
        if(result) {
            dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
        }else{
            dataWrapper.setErrorCode(ErrorCodeEnum.Error);
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> changePWD(String oldPWD, String newPWD,String sessionKey) {
        DataWrapper<Void> dataWrapper = new DataWrapper<>();
        UserEntity user = SessionManager.getSession(sessionKey);
        if(user != null){
            if(user.getPassword().equals(MD5Util.getMD5Password(oldPWD))){
                userDao.updateUserPassword(user.getId(),MD5Util.getMD5Password(newPWD));
            }
        }
        dataWrapper.setErrorCode(ErrorCodeEnum.Error);
        return dataWrapper;
    }

    @Override
    public DataWrapper<Void> forgetPWD(String username,String password,String code) {
        DataWrapper<Void> dataWrapper = new DataWrapper<>();
        if(VerifyCodeManager.getPhoneCode(username) != null){
            if(VerifyCodeManager.getPhoneCode(username).equals(code)){
                int userId = userDao.getUserByUsername(username).getId();
                userDao.updateUserPassword(userId,MD5Util.getMD5Password(code));

                VerifyCodeManager.removePhoneCodeByPhoneNum(username);

            }else{
                dataWrapper.setErrorCode(ErrorCodeEnum.Error);
            }
        }else{
            dataWrapper.setErrorCode(ErrorCodeEnum.Verify_Code_Error);
        }
        return dataWrapper;
    }

    @Override
    public DataWrapper<List<UserEntity>> getUserList() {
        //userDao.getUserList();
        return null;
    }
}

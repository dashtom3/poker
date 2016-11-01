package com.server.user;


import com.server.util.DataWrapper;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */

public interface UserService {
    DataWrapper<Void> addUser(UserEntity user);
    DataWrapper<Void> deleteUser(Long id);
    DataWrapper<Void> updateUser(UserEntity user);
    DataWrapper<List<UserEntity>> getUserList();
    DataWrapper<UserEntity> login(String username,String password);
    DataWrapper<UserEntity> register(String username,String password,String code);
    DataWrapper<Void>getVerifyCode(String phoneNum);
    DataWrapper<Void> changePWD(String oldPWD,String newPWD,String sessionKey);
    DataWrapper<Void> forgetPWD(String username,String password,String code);

}

package com.server.http.service.i;


import com.server.common.util.DataWrapper;
import com.server.http.entity.UserEntity;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */

public interface UserService {
    DataWrapper<Void> addUser(UserEntity user);
    DataWrapper<Void> deleteUser(int id);
    DataWrapper<UserEntity> updateUser(UserEntity user);
    DataWrapper<List<UserEntity>> getUserList();
    DataWrapper<UserEntity> login(String username,String password);
    DataWrapper<UserEntity> register(String username,String password,String code);
    DataWrapper<Void>getVerifyCode(String phoneNum);
    DataWrapper<Void> changePWD(String oldPWD,String newPWD,String sessionKey);
    DataWrapper<Void> forgetPWD(String username,String password,String code);
    DataWrapper<Void> addFriends(int userId,String friend);

}

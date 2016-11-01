package com.server.user;




import com.server.util.DataWrapper;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public interface UserDao {
    boolean register(UserEntity user);
    boolean deleteUser(Long id);
    boolean updateUser(UserEntity user);
    DataWrapper<List<UserEntity>> getUserList();
    UserEntity getUserByUsername(String username);
    boolean updateUserPassword(Long userId,String password);
}

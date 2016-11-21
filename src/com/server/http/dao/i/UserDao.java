package com.server.http.dao.i;




import com.server.common.util.DataWrapper;
import com.server.http.entity.UserEntity;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public interface UserDao {
    boolean register(UserEntity user);
    boolean deleteUser(int id);
    boolean updateUser(UserEntity user);
    DataWrapper<List<UserEntity>> getUserList();
    UserEntity getUserByUsername(String username);
    boolean updateUserPassword(int userId,String password);
    boolean updateUserFriends(int userId,String friends);
}

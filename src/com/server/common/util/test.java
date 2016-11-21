package com.server.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.http.entity.UserEntity;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 16/11/15.
 */
public class test {
    public static void main(String args[]) throws JSONException, JsonProcessingException {

        UserEntity user1=new UserEntity();
        UserEntity user2=new UserEntity();
        user1.setId(11);
        user2.setId(22);

        List<UserEntity> userEntityList=new ArrayList<>();
        userEntityList.add(user1);
        userEntityList.add(user2);

        String str=JSONUtil.objectMapper.writeValueAsString(userEntityList);


        System.out.println(str);

    }

}

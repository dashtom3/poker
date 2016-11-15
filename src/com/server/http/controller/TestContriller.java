package com.server.http.controller;

import com.server.common.enums.ErrorCodeEnum;
import com.server.common.util.DataWrapper;
import com.server.common.util.MD5Util;
import com.server.http.dao.i.UserDao;
import com.server.http.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value="api")
public class TestContriller {
    @Autowired
    private UserDao userDao;

	@RequestMapping(value="/testGet", method = RequestMethod.GET)
    @ResponseBody
    public DataWrapper<List<UserEntity>> getInfo() {
        DataWrapper<UserEntity> dataWrapper=new DataWrapper<>();
        UserEntity newUser = new UserEntity();
        newUser.setUsername("test");
        newUser.setPassword("xxx");

//        if (userDao.register(newUser)) {
//            System.out.println("USERname:"+newUser.getUsername());
//            dataWrapper.setData(newUser);
//        } else {
//            dataWrapper.setErrorCode(ErrorCodeEnum.Register_Error);
//        }
        return userDao.getUserList();
    }

    @RequestMapping(value="/testInster", method = RequestMethod.GET)
    @ResponseBody
    public DataWrapper<UserEntity> insterInfo() {
        DataWrapper<UserEntity> dataWrapper=new DataWrapper<>();
        UserEntity newUser = new UserEntity();
        newUser.setUsername("13162195751");
        newUser.setPassword(MD5Util.getMD5Password("123123"));
        newUser.setType(1);

        if (userDao.register(newUser)) {
            dataWrapper.setData(newUser);
        } else {
            dataWrapper.setErrorCode(ErrorCodeEnum.Register_Error);
        }
        return dataWrapper;
    }
}

package com.server.user;

import java.util.Date;

/**
 * Created by tian on 2016/10/26.
 */
public class UserGameEntity extends UserEntity {
    private Long id;
    private String username;
    private String password;
    private String realname;
    private Integer pic;
    private String score;
    private Integer allnum;
    private Integer winnum;
    private Integer gatenum;
    private Integer allinnum;
    private String rank;
    private Integer level;
    private Date registerDate;
    private Integer type;//权限
    private String gamescore;
    private String firround;
}

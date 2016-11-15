package com.server.ws.entity;

import com.server.http.entity.UserEntity;

/**
 * Created by tian on 16/10/11.
 */
public class SeatEntity {
    public short num;
    public UserEntity userEntity;
    public short state;//0:空闲 1:有人 2:弃牌
    public int[] pay = {0,0,0,0};//每轮金钱
    public int card1;
    public int card2;
    public int cardType;//9种类型
    public SeatEntity(UserEntity userEntity, short state){
        this.userEntity = userEntity;
        this.state = state;
    }

}

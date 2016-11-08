package com.server.room;

import com.server.user.UserEntity;

import javax.websocket.Session;

/**
 * Created by tian on 16/10/11.
 */
public class SeatEntity {
    public short num;
    public UserEntity userEntity;
    public short state;//0:空闲 1:有人
    public int[] pay = new int[4];//每轮金钱
    public short[][] card = new short[4][13];
    public SeatEntity(UserEntity userEntity,short state,Session session){
        this.userEntity = userEntity;
        this.state = state;
    }

    public SeatEntity(UserEntity userEntity, short i) {
    }

}

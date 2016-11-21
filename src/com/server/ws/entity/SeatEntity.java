package com.server.ws.entity;

import com.server.http.entity.UserEntity;

/**
 * Created by tian on 16/10/11.
 */
public class SeatEntity {
    public short num;
    public UserEntity userEntity;
    public int score;//在该房间的积分
    public short state;//0:空闲 1:下注 2:弃牌 3.让牌 4.allin
    public int[] pay = {0,0,0,0};//每轮下注
    public int card1;
    public int card2;//两张手牌
    public int cardType;//牌型,10种类型
    public String cardString="";//具体牌的大小,如1302表示K和2

    public SeatEntity(UserEntity userEntity, short state){
        this.userEntity = userEntity;
        this.state = state;
    }

    public String geneCardString(int num){
        if(num>9)
            return this.cardString+num;
        else
            return this.cardString+0+num;
    }

}

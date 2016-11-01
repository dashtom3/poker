package com.server.room;

import com.server.user.UserEntity;

import java.util.*;

/**
 * Created by tian on 16/10/10.
 */
public class RoomEntity {
    private List<SeatEntity> seatEntities = new ArrayList<>();
    private List<UserEntity> audience = new ArrayList<>();
    private short firPlayer = 0;//大盲
    private short state = 1;//游戏状态 1未开始 0已开始
    private List<Integer> card = new ArrayList<>();
    private Timer timer;
    private short playerNum = 0;
    private short MAXPLAYER = 6;
    private short type;
    private List<Long> userList = new ArrayList<>();

    public RoomEntity(short type){
        this.type = type;
    }


    public List<SeatEntity> getSeatEntities() {
        return seatEntities;
    }
    public void setSeatEntities(List<SeatEntity> seatEntities) {
        this.seatEntities = seatEntities;
    }

    public List<UserEntity> getAudience() {
        return audience;
    }

    public void setAudience(List<UserEntity> audience) {
        this.audience = audience;
    }

    public short getFirPlayer() {
        return firPlayer;
    }

    public void setFirPlayer(short firPlayer) {
        this.firPlayer = firPlayer;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public List<Integer> getCard() {
        return card;
    }

    public void setCard(List<Integer> card) {
        this.card = card;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public short getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(short playerNum) {
        this.playerNum = playerNum;
    }

    public short getMAXPLAYER() {
        return MAXPLAYER;
    }

    public void setMAXPLAYER(short MAXPLAYER) {
        this.MAXPLAYER = MAXPLAYER;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public List<Long> getUserList() {
        return userList;
    }

    public void setUserList(List<Long> userList) {
        this.userList = userList;
    }
}

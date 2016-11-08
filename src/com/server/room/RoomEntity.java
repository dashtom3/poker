package com.server.room;

import com.server.user.UserEntity;

import java.util.*;

import static com.server.game.GameEntity.room;
import static com.sun.tools.internal.xjc.reader.Ring.add;
import static javafx.scene.input.KeyCode.T;

/**
 * Created by tian on 16/10/10.
 */
public class RoomEntity {
    private List<SeatEntity> seatEntities = new ArrayList<>();
    private List<UserEntity> audience = new ArrayList<>();
    private short firPlayer = 0;//大盲
    private short state = 1;//游戏状态 0:进行中 1:未开始
    private List<Integer> card = new ArrayList<>();//记录一副牌
    private Timer timer;
    private short playerNum = 0;
    private short MAXPLAYER = 6;
    private short type;//房间类型
    private List<Long> userList = new ArrayList<>();//广播用户列表
    private boolean isNew = true;//是否是新房间

    public RoomEntity(short type){
        this.type = type;
    }

    //玩家坐下:若房间有空闲座位且游戏未开始,可以坐下
    public synchronized boolean sit(UserEntity user){
        if(this.state==1 && this.playerNum< this.MAXPLAYER){
            this.seatEntities.add(new SeatEntity(user, (short) 1));
            this.playerNum++;
            return true;
        }
        return false;
    }

    //玩家站起:离开座位,成为观众
    public synchronized boolean standUp(UserEntity user){
        for(SeatEntity item:seatEntities){
            if(item.userEntity.getId()==user.getId()){
                this.seatEntities.remove(item);
                this.audience.add(user);
                this.playerNum--;
                return true;
            }
        }
        return false;
    }

    //玩家离开:站起(可能是玩家可能是观众),离开房间
    public synchronized boolean leaveRoom(UserEntity user){
        standUp(user);//若是玩家先站起变成观众
        this.audience.remove(user);
        userList.remove(user.getId());
        return true;
    }

    public void addAudience(UserEntity user){
        this.audience.add(user);
    }

    public void addToUserList(long userId){
        this.userList.add(userId);
    }

    public void startGame(){
        new Thread(new Runnable() {
            boolean isDeleteRoom=false;
            @Override
            public void run() {
                while (!isDeleteRoom){
                    //每局游戏间隔5S
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //业务


                    //房间所有人离开,则删除该房间
                    if(playerNum==0)
                        isDeleteRoom=true;
                }
            }
        }).start();
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
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

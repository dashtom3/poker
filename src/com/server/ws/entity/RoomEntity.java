package com.server.ws.entity;

import com.server.http.entity.UserEntity;

import java.util.*;

/**
 * Created by tian on 16/10/10.
 */
public class RoomEntity {
    private LinkedList<SeatEntity> seatEntities = new LinkedList<>();
    private List<UserEntity> audience = new ArrayList<>();
    private short firPlayer = 0;//当前需要进行操作的玩家
    private short state = 1;//游戏状态 0:进行中 1:未开始
    private List<Integer> card = new ArrayList<>();//记录一副牌
    private short playerNum = 0;
    private short MAXPLAYER = 6;
    private short type;//房间类型(初始筹码)
    private List<Integer> userList = new ArrayList<>();//广播用户列表
    private boolean isNew = true;//是否是新房间
    private int index;//房间号
    public int round=1;//游戏进行到第几轮
    public int[] publicCard=new int[5];//记录5张公共牌
    public int allStake;//赌池总筹码
    public int bigBlind;

    public RoomEntity(short type){
        this.type = type;
    }

    public void nextPlay(){
        if(firPlayer+1==seatEntities.size())
            firPlayer=0;
        else
            firPlayer++;
    }

    public void addPlayer(UserEntity user){
        this.seatEntities.add(new SeatEntity(user, (short) 1));
        this.playerNum++;
    }
    public void deletePlayer(SeatEntity seat){
        this.seatEntities.remove(seat);
        this.playerNum--;
    }

    public void addAudience(UserEntity user){
        this.audience.add(user);
    }
    public void deleteAudience(UserEntity user){
        this.audience.remove(user);
    }

    public void addToUserList(Integer userId){
        this.userList.add(userId);
    }
    public void deleteUserList(Integer userId){
        this.userList.remove(userId);
    }

    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public LinkedList<SeatEntity> getSeatEntities() {
        return seatEntities;
    }
    public void setSeatEntities(LinkedList<SeatEntity> seatEntities) {
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

    public List<Integer> getUserList() {
        return userList;
    }

    public void setUserList(List<Integer> userList) {
        this.userList = userList;
    }
}

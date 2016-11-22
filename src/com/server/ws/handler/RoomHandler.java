package com.server.ws.handler;

import com.server.common.util.JSONUtil;
import com.server.http.entity.UserEntity;
import com.server.ws.core.server.WebSocketForPad;
import com.server.ws.entity.RoomEntity;
import com.server.ws.entity.SeatEntity;
import java.io.IOException;

import static com.server.ws.entity.GameEntity.roomList;

/**
 * Created by joseph on 16/11/9.
 */
public class RoomHandler {

    private GameHandler gameHandler=new GameHandler();

    //判断是否有房间可用
    public synchronized void joinGame(short type, UserEntity user) throws IOException {
        //遍历所有房间,找到相应等级有座位且未开始游戏的房间,进入房间
        Boolean isFindRoom=false;
        RoomEntity currRoom = null;
        for(int i = 0; i< roomList.size(); i++){
            currRoom= roomList.get(i);
            if(type == currRoom.getType() && currRoom.getPlayerNum()<currRoom.getMAXPLAYER() && currRoom.getState()==1){
                addPlayerToRoom(currRoom,user);
                isFindRoom=true;
                break;
            }
        }
        //否则创建新房间
        if(!isFindRoom)
            createRoom(type,user);
    }

    //玩家进入房间
    public void addPlayerToRoom(RoomEntity roomEntity,UserEntity userEntity) throws IOException {
        final int index = roomList.indexOf(roomEntity);
        userEntity.setRoomIndex(index);
        //1.加入到广播列表
        roomEntity.addToUserList(userEntity.getId());
        //2.有座位且积分大于最低要求则成为玩家,进入房间扣除相应积分
        int limitScore=roomEntity.getType()*100;
        if(userEntity.getScore()>=limitScore&&roomEntity.getState()==1 && roomEntity.getPlayerNum()< roomEntity.getMAXPLAYER()){
            if(sit(userEntity)){
                if(roomEntity.getType()==6){//无上限房间:所有积分带入房间
                    limitScore=userEntity.getScore();
                    userEntity.setScore(0);
                }
                else {//普通房间
                    userEntity.setScore(userEntity.getScore()-limitScore);
                }
                for(SeatEntity seat:roomEntity.getSeatEntities())
                    if(seat.userEntity.getId()==userEntity.getId()){
                        seat.score=limitScore;
                        WebSocketForPad.broadcastMessage(roomEntity.getUserList(),userEntity.getUsername()+" join game,room: "+roomEntity.getIndex());
                        break;
                    }
            }
            else {//坐下失败成为观众
                roomEntity.addAudience(userEntity);
                WebSocketForPad.broadcastMessage(roomEntity.getUserList(),userEntity.getUsername()+" join audience,room: "+roomEntity.getIndex());
            }
        }
        //3.否则成为观众
        else{
            roomEntity.addAudience(userEntity);
            WebSocketForPad.broadcastMessage(roomEntity.getUserList(),userEntity.getUsername()+" join audience,room: "+roomEntity.getIndex());
        }
        //4.向新加入玩家通知所有老玩家信息,向所有老玩家广播新加入玩家信息
        String info= JSONUtil.objectMapper.writeValueAsString(roomEntity.getSeatEntities());
        WebSocketForPad.sendMessage(userEntity.getId(),info);
        info= JSONUtil.objectMapper.writeValueAsString(userEntity);
        WebSocketForPad.broadcastMessage(roomEntity.getUserList(),info);

        //5.若房间有2个人且是新房间,则启动游戏进程,否则该房间游戏进程已开启等待下一局就行
        if(roomEntity.getPlayerNum()>1&&roomEntity.isNew()==true)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gameHandler.startGame(index);
                }
            }).start();

    }

    //创建房间
    public void createRoom(short type,UserEntity userEntity) throws IOException {
        RoomEntity roomEntity = new RoomEntity(type);
        roomList.add(roomEntity);
        roomEntity.setIndex(roomList.indexOf(roomEntity));
        System.out.println("******"+userEntity.getUsername()+"  create room success,room index:"+roomEntity.getIndex());
        addPlayerToRoom(roomEntity,userEntity);
    }

    //玩家坐下:若房间有空闲座位且游戏未开始,可以坐下
    public synchronized boolean sit(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room = roomList.get(roomIndex);
        if(room.getState()==1 && room.getPlayerNum()< room.getMAXPLAYER()){
            room.addPlayer(user);
            WebSocketForPad.broadcastMessage(room.getUserList(),user.getUsername()+" sit success");
            return true;
        }
        //sit失败则只通知自己
        WebSocketForPad.sendMessage(user.getId(),user.getUsername()+" sit fail");
        return false;
    }

    //玩家站起:离开座位,成为观众
    public synchronized void standUp(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room = roomList.get(roomIndex);
        for(SeatEntity item:room.getSeatEntities()){
            if(item.userEntity.getId()==user.getId()){
                room.deletePlayer(item);
                room.addAudience(user);
                WebSocketForPad.broadcastMessage(room.getUserList(),user.getUsername()+" standUp success");
                return;
            }
        }
        //standUp失败则只通知自己
        WebSocketForPad.sendMessage(user.getId(),user.getUsername()+" standUp fail");
    }

    //玩家离开:站起(可能是玩家可能是观众),离开房间
    public synchronized void leaveRoom(UserEntity user) throws IOException {
        int roomIndex=user.getRoomIndex();
        RoomEntity room = roomList.get(roomIndex);
        for(SeatEntity seat:room.getSeatEntities())//离场退还积分
            if(seat.userEntity.getId()==user.getId()){
                user.setScore(user.getScore()+seat.score);
                WebSocketForPad.sendMessage(user.getId(),"退回积分:"+seat.score);
                break;
            }
        standUp(user);//若是玩家先站起变成观众
        room.deleteAudience(user);
        room.deleteUserList(user.getId());
        WebSocketForPad.broadcastMessage(room.getUserList(),user.getUsername()+" leave success");
    }

}

package com.server.room;

import com.server.game.GameEntity;
import com.server.user.UserEntity;
import com.server.websocket.server.WebSocketForPad;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.server.game.GameEntity.roomList;

/**
 * Created by joseph on 16/11/9.
 */
public class RoomHandler {

    private WebSocketForPad webSocket = new WebSocketForPad();

    //判断是否有房间可用
    public synchronized List<Long> joinGame(short type, UserEntity user){
        //遍历所有房间,找到相应等级有座位且未开始游戏的房间,进入房间
        RoomEntity currRoom = null;
        for(int i = 0; i< roomList.size(); i++){
            currRoom= roomList.get(i);
            if(type == currRoom.getType() && currRoom.getPlayerNum()<currRoom.getMAXPLAYER() && currRoom.getState()==1){
                addPlayerToRoom(currRoom,user);
                return currRoom.getUserList();
            }
        }
        //否则创建新房间
        currRoom = createRoom(type,user);
        return currRoom.getUserList();
    }

    //玩家进入房间
    public void addPlayerToRoom(RoomEntity roomEntity,UserEntity userEntity){
        int index = roomList.indexOf(roomEntity);
        userEntity.roomIndex= index;
        if(!sit(index, userEntity))//有座位则成为玩家,否则成为观众
            roomEntity.addAudience(userEntity);
        //加入到广播列表
        roomEntity.addToUserList(userEntity.getId());
        //若房间有2个人且是新房间,则启动游戏进程,否则该房间游戏进程已开启等待下一局就行
        if(roomEntity.getPlayerNum()>1&&roomEntity.isNew()==true)
            startGame(roomList.indexOf(roomEntity));
    }

    //创建房间
    public RoomEntity createRoom(short type,UserEntity userEntity){
        RoomEntity roomEntity = new RoomEntity(type);
        roomList.add(roomEntity);
        roomEntity.setIndex(roomList.indexOf(roomEntity));
        addPlayerToRoom(roomEntity,userEntity);
        return roomEntity;
    }

    //玩家坐下:若房间有空闲座位且游戏未开始,可以坐下
    public synchronized boolean sit(int roomIndex, UserEntity user){
        RoomEntity room = roomList.get(roomIndex);
        if(room.getState()==1 && room.getPlayerNum()< room.getMAXPLAYER()){
            room.addPlayer(user);
            return true;
        }
        return false;
    }

    //玩家站起:离开座位,成为观众
    public synchronized boolean standUp(int roomIndex, UserEntity user){
        RoomEntity room = roomList.get(roomIndex);
        for(SeatEntity item:room.getSeatEntities()){
            if(item.userEntity.getId()==user.getId()){
                room.deletePlayer(item);
                room.addAudience(user);
                return true;
            }
        }
        return false;
    }

    //玩家离开:站起(可能是玩家可能是观众),离开房间
    public synchronized boolean leaveRoom(int roomIndex, UserEntity user){
        RoomEntity room = roomList.get(roomIndex);
        standUp(roomIndex,user);//若是玩家先站起变成观众
        room.deleteAudience(user);
        room.deleteUserList(user.getId());
        return true;
    }

    //开始游戏
    public void startGame(final int index){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    //每局游戏间隔5S
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //每局游戏开始前更新房间
                    RoomEntity room= roomList.get(index);
                    room.setState((short) 0);
                    //房间所有人离开,则删除该房间
                    if(room.getPlayerNum()==0)
                        break;

                    //***************游戏逻辑处理开始**************//
                    //1.发送给房间里所有用户:游戏开始
                    try {
                        WebSocketForPad.broadcastMessage(room.getUserList(),"start Game!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //2.第一轮:每人发2张牌,通知大盲后一位玩家下注,直到所有玩家下注相等
                    LinkedList<SeatEntity> allSeat=room.getSeatEntities();
                    for(int i=0;i<allSeat.size();i++){
                        SeatEntity currSeat=allSeat.get(i);
                        currSeat.card1 = getRandomCard(room.getCard());
                        currSeat.card2 = getRandomCard(room.getCard());
                        try {
                            WebSocketForPad.sendMessage(currSeat.userEntity.getId(),"card1:"+currSeat.card1+"   card2:"+currSeat.card2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //从小盲开始下注
                    int firstStake=room.getFirPlayer()<allSeat.size() ? room.getFirPlayer():allSeat.size();
                    try {
                        SeatEntity currSeat=allSeat.get(firstStake);
                        currSeat.pay[1]=room.getType();
                        WebSocketForPad.sendMessage(currSeat.userEntity.getId(),"小盲下注"+room.getType());

                        currSeat=allSeat.get(firstStake+1);
                        currSeat.pay[1]=room.getType()*2;
                        WebSocketForPad.sendMessage(currSeat.userEntity.getId(),"大盲下注"+room.getType()*2);

                        currSeat=allSeat.get(firstStake+2);
                        WebSocketForPad.sendMessage(currSeat.userEntity.getId(),"请选择下注/弃牌/让牌");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //3.第二轮:发3张公共牌,通知下一位玩家下注,直到所有玩家下注相等

                    //4.第三轮:发1张公共牌,通知下一位玩家下注,直到所有玩家下注相等

                    //5.第四轮:发1张公共牌,计算赢家,扣除积分,通知所有用户:游戏结束

                    //***************游戏逻辑处理结束**************//
                    room.setFirPlayer((short)0);//???????who is the first player in the next game?
                    room.setState((short) 1);
                }
            }
        }).start();
    }

    //生成一张牌
    public int getRandomCard(List<Integer> currCard){
        while (true){
            Random r = new Random();
            int temp = r.nextInt(54);
            if(currCard.contains(temp)){
                getRandomCard(currCard);
            }else{
                currCard.add(temp);
                return temp;
            }
        }
    }

    //下注
    public void stake(int roomIndex,UserEntity user,int stake){
        RoomEntity room=roomList.get(roomIndex);

    }



}

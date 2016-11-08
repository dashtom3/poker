package com.server.message;

import com.server.enums.CMDConstant;
import com.server.game.GameEntity;
import com.server.room.RoomEntity;
import com.server.room.SeatEntity;
import com.server.sessionManager.SessionManager;
import com.server.user.UserEntity;
import com.server.user.UserGameEntity;
import com.server.util.AESUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.JSONObject;

import java.util.*;

import static com.sun.tools.internal.xjc.reader.Ring.add;

/**
 * Created by tian on 2016/10/19.
 */
public class GameHandler extends MsdHandler {


    @Override
    public RespEntity handleMsg(MsgEntity msgEntity) {
        RespEntity respEntity=new RespEntity();
        UserEntity userEntity = SessionManager.getSession( msgEntity.getToken());
        Boolean isSuccess;
        List<Long> broadcastUserList=new ArrayList<>();

        //TODO message data 加密解密
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(AESUtil.decrypt(msgEntity.getData(),"")));
            RoomEntity room = (RoomEntity)jsonObject.get("room");
            switch ((int)msgEntity.getCmdCode()) {// 根据命令码对应找到对应处理方法
                case CMDConstant.GAME_JOIN:
                    broadcastUserList = joinGame((Short) jsonObject.get("type"),userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_JOIN);
                    respEntity.setData(userEntity.getUsername()+"join game success");
                    respEntity.setUserList(broadcastUserList);
                    break;
                case CMDConstant.GAME_SIT:
                    isSuccess=room.sit(userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_SIT);
                    if(isSuccess){
                        respEntity.setData(userEntity.getUsername()+"sit success");
                        respEntity.setUserList(room.getUserList());
                    }
                    else {//sit失败则只通知自己
                        respEntity.setData("sit fail");
                        broadcastUserList.add(userEntity.getId());
                        respEntity.setUserList(broadcastUserList);
                    }
                    break;
                case CMDConstant.GAME_STAND:
                    isSuccess=room.standUp(userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_STAND);
                    if(isSuccess){
                        respEntity.setData(userEntity.getUsername()+"standUp success");
                        respEntity.setUserList(room.getUserList());
                    }
                    else {//失败则只通知自己
                        respEntity.setData("standUp fail");
                        broadcastUserList.add(userEntity.getId());
                        respEntity.setUserList(broadcastUserList);
                    }
                    break;
                case CMDConstant.GAME_LEAVE:
                    room.leaveRoom(userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_LEAVE);
                    respEntity.setData(userEntity.getUsername()+"leave success");
                    respEntity.setUserList(room.getUserList());
                    break;
                case CMDConstant.GAME_CHANGE:
                    room.leaveRoom(userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_LEAVE);
                    respEntity.setData(userEntity.getUsername()+"leave success");
                    respEntity.setUserList(room.getUserList());
                    break;
                case CMDConstant.GAME_START:
                    break;
                case CMDConstant.GAME_OPERATION:
                    break;
                case CMDConstant.GAME_ROUND1:
                    break;
                case CMDConstant.GAME_ROUND2:
                    break;
                case CMDConstant.GAME_ROUND3:
                    break;
                case CMDConstant.GAME_END:
                    break;
                default:
                    System.out.println("找不到对应的命令码");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return respEntity;
    }

    //判断是否有房间可用
    public synchronized List<Long> joinGame(short type, UserEntity user){
        //遍历所有房间,找到相应等级有座位且未开始游戏的房间,进入房间
        RoomEntity currRoom = null;
        for(int i=0;i<GameEntity.room.size();i++){
            currRoom=GameEntity.room.get(i);
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
        if(!roomEntity.sit(userEntity))//有座位则成为玩家,否则成为观众
            roomEntity.addAudience(userEntity);
        //加入到广播列表
        roomEntity.addToUserList(userEntity.getId());
    }

    //创建房间
    public RoomEntity createRoom(short type,UserEntity userEntity){
        RoomEntity roomEntity = new RoomEntity(type);
        addPlayerToRoom(roomEntity,userEntity);
        GameEntity.room.add(roomEntity);
        return roomEntity;
    }





//    //计时操作
//    public void startTimer() {
//        this.timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                System.out.println("-------游戏玩家--------");
//
//            }
//        }, 1500);
//    }
//
//    //开始游戏
//    public void startGame() {
//        this.giveCard();
//        state = 0;
//
//        startTimer();
//
//        //TODO发送通知消息
//
//    }

//    //玩家操作 type 0加筹码 1all-in 2跟 3让 4弃
//    public void playerOperate(int type, int money) {
//
//    }
//
//
//
//
//    //玩家加筹码
//    public void add(){
//
//    }
//    //发牌
//    public void giveCard(){
//        for(int i=0;i<seatEntities.size();i++){
//            seatEntities.get(i).card[0] = getRandomCard();
//            seatEntities.get(i).card[1] = getRandomCard();
//        }
//    }
//    //生成一张牌
//    public int getRandomCard(){
//        while (true){
//            Random r = new Random();
//            int temp = r.nextInt(54);
//            if(card.contains(temp)){
//                getRandomCard();
//            }else{
//                card.add(temp);
//                return temp;
//            }
//        }
//    }
}

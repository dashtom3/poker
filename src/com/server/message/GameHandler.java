package com.server.message;

import com.server.enums.CMDConstant;
import com.server.room.RoomHandler;
import com.server.sessionManager.SessionManager;
import com.server.user.UserEntity;
import com.server.util.AESUtil;
import org.json.JSONObject;
import java.util.*;

/**
 * Created by tian on 2016/10/19.
 */
public class GameHandler extends MsdHandler {

    private RoomHandler roomHandler = new RoomHandler();

    @Override
    public RespEntity handleMsg(MsgEntity msgEntity) {
        RespEntity respEntity=new RespEntity();
        UserEntity userEntity = SessionManager.getSession( msgEntity.getToken());
        int roomIndex=userEntity.roomIndex;
        Boolean isSuccess;
        List<Long> broadcastUserList=new ArrayList<>();
        int stake;

        //TODO message data 加密解密
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(AESUtil.decrypt(msgEntity.getData(),"")));

            switch ((int)msgEntity.getCmdCode()) {// 根据命令码对应找到对应处理方法
                case CMDConstant.GAME_JOIN:
                    broadcastUserList = roomHandler.joinGame((Short) jsonObject.get("type"),userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_JOIN);
                    respEntity.setData(userEntity.getUsername()+"join game success");
                    respEntity.setUserList(broadcastUserList);
                    break;
                case CMDConstant.GAME_SIT:
                    isSuccess=roomHandler.sit(roomIndex,userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_SIT);
                    if(isSuccess){
                        respEntity.setData(userEntity.getUsername()+"sit success");
                        //respEntity.setUserList(room.getUserList());
                    }
                    else {//sit失败则只通知自己
                        respEntity.setData("sit fail");
                        broadcastUserList.add(userEntity.getId());
                        respEntity.setUserList(broadcastUserList);
                    }
                    break;
                case CMDConstant.GAME_STAND:
                    isSuccess=roomHandler.standUp(roomIndex,userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_STAND);
                    if(isSuccess){
                        respEntity.setData(userEntity.getUsername()+"standUp success");
                        //respEntity.setUserList(room.getUserList());
                    }
                    else {//失败则只通知自己
                        respEntity.setData("standUp fail");
                        broadcastUserList.add(userEntity.getId());
                        respEntity.setUserList(broadcastUserList);
                    }
                    break;
                case CMDConstant.GAME_LEAVE:
                    roomHandler.leaveRoom(roomIndex,userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_LEAVE);
                    respEntity.setData(userEntity.getUsername()+"leave success");
                    //respEntity.setUserList(room.getUserList());
                    break;
                case CMDConstant.GAME_CHANGE:
                    roomHandler.leaveRoom(roomIndex,userEntity);
                    broadcastUserList = roomHandler.joinGame((Short) jsonObject.get("type"),userEntity);
                    respEntity.setCmdCode(CMDConstant.GAME_LEAVE);
                    respEntity.setData(userEntity.getUsername()+"change room success");
                    respEntity.setUserList(broadcastUserList);
                    break;
                case CMDConstant.GAME_STAKE:
                    stake=jsonObject.getInt("stake");
                    break;
                case CMDConstant.GAME_ABANDON:
                    break;
                case CMDConstant.GAME_SKIP:
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
}

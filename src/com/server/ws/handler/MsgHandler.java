package com.server.ws.handler;

import com.server.common.enums.CMDConstant;
import com.server.common.sessionManager.SessionManager;
import com.server.http.entity.UserEntity;
import com.server.ws.entity.MsgEntity;
import org.json.JSONObject;

/**
 * Created by tian on 2016/10/19.
 */
public class MsgHandler {
    private RoomHandler roomHandler = new RoomHandler();
    private GameHandler gameHandler = new GameHandler();

    public void handleMsg(MsgEntity msgEntity) {
        UserEntity userEntity = SessionManager.getSession(msgEntity.getToken());
        short type;

        //TODO message data 加密解密
        try {
            //JSONObject jsonObject = new JSONObject(String.valueOf(AESUtil.decrypt(msgEntity.getData(),"")));
            JSONObject jsonObject = new JSONObject();

            switch (msgEntity.getCmdCode()) {// 根据命令码对应找到对应处理方法
                case CMDConstant.GAME_JOIN:
                    type=Short.parseShort(String.valueOf(msgEntity.getData().get("type")));
                    roomHandler.joinGame(type,userEntity);
                    break;
                case CMDConstant.GAME_SIT:
                    roomHandler.sit(userEntity);
                    break;
                case CMDConstant.GAME_STAND:
                    roomHandler.standUp(userEntity);
                    break;
                case CMDConstant.GAME_LEAVE:
                    roomHandler.leaveRoom(userEntity);
                    break;
                case CMDConstant.GAME_CHANGE:
                    type=Short.parseShort(String.valueOf(msgEntity.getData().get("type")));
                    roomHandler.leaveRoom(userEntity);
                    roomHandler.joinGame(type,userEntity);
                    break;
                case CMDConstant.GAME_STAKE:
                    int stake=msgEntity.getData().getInt("stake");
                    gameHandler.stake(userEntity,stake);
                    break;
                case CMDConstant.GAME_ABANDON:
                    gameHandler.abandon(userEntity);
                    break;
                case CMDConstant.GAME_SKIP:
                    gameHandler.skip(userEntity);
                    break;
                default:
                    System.out.println("找不到对应的命令码");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

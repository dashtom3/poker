package com.server.message;

import com.server.enums.CMDConstant;
import com.server.game.GameEntity;
import com.server.room.RoomEntity;
import com.server.room.SeatEntity;
import com.server.sessionManager.SessionManager;
import com.server.user.UserEntity;
import com.server.user.UserGameEntity;
import com.server.util.AESUtil;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by tian on 2016/10/19.
 */
public class GameHandler extends MsdHandler {


    @Override
    public void handleMsg(MsgEntity msgEntity, RespEntity respEntity) {
        UserEntity userEntity = SessionManager.getSession( msgEntity.getToken());

        //TODO message data 加密解密
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(AESUtil.decrypt(msgEntity.getData(),"")));
            switch ((int)msgEntity.getCmdCode()) {// 根据命令码对应找到对应处理方法
                case CMDConstant.GAME_JOIN:
                    List<Long> respUsers = new ArrayList<Long>();
                    this.joinGame((Short) jsonObject.get("type"),userEntity,respUsers);

                    UserGameEntity userGameEntity = (UserGameEntity) userEntity;

                    respEntity.setCmdCode(CMDConstant.GAME_JOIN);
                    respEntity.setData(new JSONObject(userGameEntity).toString());
                    respEntity.setUserList(respUsers);

                    break;
                case CMDConstant.GAME_SIT:

                    break;
                case CMDConstant.GAME_STAND:
                    break;
                case CMDConstant.GAME_LEAVE:
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
    }
    //加入游戏
    public synchronized void joinGame(short type, UserEntity user,List<Long> responers){
        //匹配
        RoomEntity roomEntity = null;
        boolean gameState = false;
        for(int i=0;i<GameEntity.room.size();i++){
            if(GameEntity.room.get(i).getPlayerNum()<GameEntity.room.get(i).getMAXPLAYER() && type == GameEntity.room.get(i).getType()){
                this.playerAdd(GameEntity.room.get(i),user);
                gameState = true;
                roomEntity = GameEntity.room.get(i);
                break;
            }
        }
        if(gameState == false){
            roomEntity = roomAdd(type,user);
        }
        responers = roomEntity.getUserList();
    }
    //玩家加入
    public synchronized void playerAdd(RoomEntity roomEntity,UserEntity userEntity){
        if(roomEntity.getPlayerNum() < roomEntity.getMAXPLAYER()){
            short playerNum = (short) (roomEntity.getPlayerNum() + 1);
            roomEntity.setPlayerNum(playerNum);
            roomEntity.getSeatEntities().add(new SeatEntity(userEntity, (short) 0));
            //开始游戏
            if(roomEntity.getState() == 1 && playerNum>1){
                //startGame();
            }
        }else {
            roomEntity.getAudience().add(userEntity);
        }
        roomEntity.getUserList().add(userEntity.getId());
    }
//    //玩家坐下
//    public synchronized boolean playerSit(){
//        if(playerNum < MAXPLAYER ){
//
//        }
//        return true;
//    }
//    //玩家站起
//    public synchronized boolean playerStandUp(){
//        return true;
//    }
//    //玩家离开
//    public synchronized boolean playerLeave(){
//        return true;
//    }
    //创建房间
    public synchronized RoomEntity roomAdd(short type,UserEntity userEntity){
        RoomEntity roomEntity = new RoomEntity(type);
        this.playerAdd(roomEntity,userEntity);
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

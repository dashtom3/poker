//package com.server.message;
//
//import GameEntity;
//import SessionManager;
//import UserEntity;
//import org.json.JSONException;
//import org.json.JSONObject;
//
////import javax.jms.Message;
//
///**
// * Created by tian on 16/10/13.
// */
//public class MessageServiceImpl implements MessageService{
//
//    @Override
//    public JSONObject AnalyseMessage(String message) {
//        try {
//            JSONObject jsonObject = new JSONObject(message);
//            UserEntity userEntity = SessionManager.getSession((String) jsonObject.get("token"));
//            if(userEntity == null){
//
//            }else{
//                String msd = (String) jsonObject.get("msd");
//                String params = (String) jsonObject.get("params");
//                switch(msd){
//                    //进入
//                    case "1001":
//                        JSONObject jsonParams = new JSONObject(params);
//                        GameEntity.joinGame((Integer) jsonParams.get("type"),userEntity);
//                        break;
//                    //坐下
//                    case "1002":
//                        break;
//                    //站起
//                    case "1003":
//                        break;
//                    //离开
//                    case "1004":
//                        break;
//                    //游戏操作 加筹码 让 弃牌
//                    case "2001":
//                        break;
//                    //服务器端编码
//                    //加筹码超时
//                    //返回房间信息
//                    //各编码返回信息
//                }
//            }
//            return jsonObject;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    //1001 有人进入房间 1002 有人坐下 1003 有人站起 1004 有人离开 1005 某用户操作 1006 发牌 1007 房间信息 1008 游戏开始 1009 游戏结束
//    public String returnMessage(String code,String data){
//        switch(code){
//            //进入
//            case "1001":
//                JSONObject jsonParams = new JSONObject(params);
//                GameEntity.joinGame((Integer) jsonParams.get("type"),userEntity);
//                break;
//            //坐下
//            case "1002":
//                break;
//            //站起
//            case "1003":
//                break;
//        }
//    }
//
//}

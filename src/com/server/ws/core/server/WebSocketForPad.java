package com.server.ws.core.server;
import com.server.common.sessionManager.SessionManager;
import com.server.http.entity.UserEntity;
import com.server.ws.handler.GameHandler;
import com.server.ws.handler.MsdHandler;
import com.server.ws.entity.MsgEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;



 
@ServerEndpoint("/websocket/{token}")
public class WebSocketForPad {

    private static int onlineCount = 0;
     
    //private static CopyOnWriteArraySet<WebSocketForPad> webSocketSet = new CopyOnWriteArraySet<WebSocketForPad>();

    private static HashMap<Integer,Session>  sessionMap = new HashMap<>();

    private String id;

    private MsdHandler msgHandler = new GameHandler();


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token){

        UserEntity user = SessionManager.getSession(token);
        if(user == null){
            //TODO websocket 检查服务器如何关闭
            try {
                session.getBasicRemote().sendText("please log in..");
                session.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            addOnlineCount();
            sessionMap.put(user.getId(),session);
            System.out.println("#####"+user.getUsername()+"connect to ws success,token:"+token);
        }
    }
     
    @OnClose
    public void onClose() {
        //webSocketSet.remove(this);
       // subOnlineCount();
    }
     
    @OnMessage
    public void onMessage(String message, @PathParam("token") String token, Session session) throws IOException {
        MsgEntity msgEntity = new MsgEntity(message);
        msgEntity.setToken(token);
        msgHandler.handleMsg(msgEntity);
//        System.out.println("###server get:"+message);
//        this.session.getBasicRemote().sendText("###server get:"+message);
    }
    
    
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("Conntion Error!");
        error.printStackTrace();
    }
     

    public static void sendMessage(int userId, String message) throws IOException{
        sessionMap.get(userId).getBasicRemote().sendText(message);
        //this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static void broadcastMessage(List<Integer> userList, String message) throws IOException{
        for(int userId:userList)
            sendMessage(userId,message);
    }

//    public static void sendMessageTo(String message,String toList) {
//      for(WebSocketForPad item: webSocketSet) {
//    	  if(!toList.contains(item.getId()))
//      		continue;
//          try {
//              item.sendMessage(message);
//          } catch (IOException e) {
//              e.printStackTrace();
//              continue;
//          }
//      }
//    }
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
        WebSocketForPad.onlineCount++;
    }
     
    public static synchronized void subOnlineCount() {
        WebSocketForPad.onlineCount--;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    
}

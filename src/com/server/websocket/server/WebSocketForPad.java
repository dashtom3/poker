package com.server.websocket.server;
import com.server.message.*;
import com.server.sessionManager.SessionManager;
import com.server.user.UserEntity;

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

    private static HashMap<Long,Session>  sessionMap = new HashMap<>();

    private Session session;

    private String id;

    private MsdHandler msgHandler = new GameHandler();


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token){

        UserEntity user = SessionManager.getSession(token);
        if(user == null){
            //TODO websocket 检查服务器如何关闭
            try {
                session.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            addOnlineCount();
            sessionMap.put(user.getId(),session);
        }
        this.session = session;
        //webSocketSet.add(this);
    }
     
    @OnClose
    public void onClose() {
        //webSocketSet.remove(this);
       // subOnlineCount();
    }
     
    @OnMessage
    public void onMessage(String message, Session session) {
//        messageService.AnalyseMessage(message);

//        for(WebSocketForPad item: webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//                continue;
//            }
//        }

        MsgEntity msgEntity = new MsgEntity(message);
        RespEntity respEntity=msgHandler.handleMsg(msgEntity);

        List<Long> userList = respEntity.getUserList();
        for(int i=0;i<userList.size();i++){
            try {//广播
                sessionMap.get(userList.get(i)).getBasicRemote().sendText(respEntity.getData());
            }catch (IOException e){
                e.printStackTrace();
                continue;
            }
        }
    }
    
    
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("Conntion Error!");
        error.printStackTrace();
    }
     

    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
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

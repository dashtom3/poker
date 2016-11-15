package com.server.ws.entity;

import org.json.JSONObject;

/**
 * Created by tian on 2016/10/19.
 */
public class MsgEntity {
    private Integer cmdCode;// 储存命令码
    private JSONObject data;// 存放实际数据,用于protobuf解码成对应message
    private String token;// 当前玩家的channel
    public MsgEntity(String message){
        try {
            JSONObject jsonObject = new JSONObject(message);
            this.cmdCode = (Integer) jsonObject.get("code");
            this.data = (JSONObject) jsonObject.get("data");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Integer getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(Integer cmdCode) {
        this.cmdCode = cmdCode;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

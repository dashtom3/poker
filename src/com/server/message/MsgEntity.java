package com.server.message;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.hibernate.action.spi.Executable;
import org.json.JSONObject;

/**
 * Created by tian on 2016/10/19.
 */
public class MsgEntity {
    private short cmdCode;// 储存命令码
    private String data;// 存放实际数据,用于protobuf解码成对应message
    private String token;// 当前玩家的channel
    public MsgEntity(String message){
        try {
            JSONObject jsonObject = new JSONObject(message);
            this.cmdCode = (short) jsonObject.get("code");
            this.data = (String) jsonObject.get("data");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public short getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(short cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

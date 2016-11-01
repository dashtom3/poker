package com.server.message;

import java.util.List;

/**
 * Created by tian on 2016/10/21.
 */
public class RespEntity {
    private short cmdCode;// 储存命令码
    private String data;// 存放实际数据,用于protobuf解码成对应message
    private List<Long> userList;// 当前玩家的channel

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

    public List<Long> getUserList() {
        return userList;
    }

    public void setUserList(List<Long> userList) {
        this.userList = userList;
    }
}

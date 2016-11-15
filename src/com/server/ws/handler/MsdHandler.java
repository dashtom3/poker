package com.server.ws.handler;

import com.server.ws.entity.MsgEntity;
import com.server.ws.entity.RespEntity;

/**
 * Created by tian on 2016/10/19.
 */
public abstract class MsdHandler {
    public abstract RespEntity handleMsg(MsgEntity msgEntity);

    public MsdHandler(){

    }
}

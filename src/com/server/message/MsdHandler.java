package com.server.message;

import java.util.List;

/**
 * Created by tian on 2016/10/19.
 */
public abstract class MsdHandler {
    public abstract void handleMsg(MsgEntity msgEntity, RespEntity respEntity);

    public MsdHandler(){

    }
}

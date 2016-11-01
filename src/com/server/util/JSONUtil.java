package com.server.util;

import org.json.JSONObject;

/**
 * Created by tian on 2016/10/26.
 */
public class JSONUtil<T> {
    public String entityToStr(T t){
        JSONObject jsonObject = new JSONObject(t);
        return jsonObject.toString();
    }

}

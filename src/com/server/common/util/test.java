package com.server.common.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joseph on 16/11/15.
 */
public class test {
    public static void main(String args[]) throws JSONException {
        //String mesg="{\"code\":108,\"data\":{\"type\":1,\"stake\":5}}";
        String mesg="{\"code\":108,\"data\":{}}";
        JSONObject jsonObject  = new JSONObject(mesg);
        JSONObject data = (JSONObject) jsonObject.get("data");

//        int stake=data.getInt("stake");
//        short type=Short.parseShort(String.valueOf(data.get("type")));
//        System.out.println(stake);
        System.out.println(data);

    }

}

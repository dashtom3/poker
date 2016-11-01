package com.server.sessionManager;

import com.server.util.TimeUtil;

import java.util.*;

/**
 * Created by tian on 16/10/10.
 */
public class VerifyCodeManager {
    private static int minute = 5;
    private static HashMap<String, String> USER_CODE_MAP = new HashMap<String, String>();
    public static String newPhoneCode(String phoneNum) {
        Random random = new Random();
        int a = random.nextInt(8999)+1000;
        String code = String.valueOf(a);
        String oldCode = getPhoneCode(phoneNum);
        Date nowTime = new Date();
        if(oldCode!=null){
            try {
                if(TimeUtil.timeBetween(TimeUtil.changeStringToDate(oldCode.substring(3)),nowTime)/(60*1000)>minute){
                    USER_CODE_MAP.put(phoneNum,code+TimeUtil.changeDateToString(nowTime));
                    return oldCode;
                }else{
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        USER_CODE_MAP.put(phoneNum,code+TimeUtil.changeDateToString(nowTime));
        return code;
    }

    public static String getPhoneCode(String phoneNum){
        try {
            if (TimeUtil.timeBetween(TimeUtil.changeStringToDate(USER_CODE_MAP.get(phoneNum).substring(3)), new Date()) / (60 * 1000) > minute){
                VerifyCodeManager.removePhoneCodeByPhoneNum(phoneNum);
                return null;
            }else{
                return  USER_CODE_MAP.get(phoneNum).substring(0,3);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除某用户的验证码Code
     */
    public static void removePhoneCodeByPhoneNum(String phoneNum) {
        if (USER_CODE_MAP.containsKey(phoneNum)) {
            USER_CODE_MAP.remove(phoneNum);
        }
    }

}

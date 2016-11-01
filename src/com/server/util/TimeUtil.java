package com.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tian on 16/10/18.
 */
public class TimeUtil {
    public static String changeDateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeString = sdf.format(date);
        return timeString;
    }
    public static Date changeStringToDate(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.parse(time);
    }
    public static Long timeBetween(Date oldDate, Date nowDate){
        return Math.abs(nowDate.getTime()-oldDate.getTime());
    }
}

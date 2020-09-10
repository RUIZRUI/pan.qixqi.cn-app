package cn.qixqi.pan.util;

import android.content.Context;

import cn.qixqi.pan.bean.Sessions;

public class SessionsUtil {

    public static int getTheOtherId(Context context, Sessions sessions){
        int selfId = SharedPreferenceUtil.getUserId(context);
        return (sessions.getUserId1() == selfId) ? sessions.getUserId2() : sessions.getUserId1();
    }

    public static int getTheOtherId(int selfId, Sessions sessions){
        return (sessions.getUserId1() == selfId) ? sessions.getUserId2() : sessions.getUserId1();
    }

    public static String getTheOtherName(Context context, Sessions sessions){
        int selfId = SharedPreferenceUtil.getUserId(context);
        return (sessions.getUserId1() == selfId) ? sessions.getUsername2() : sessions.getUsername1();
    }

    public static String getTheOtherName(int selfId, Sessions sessions){
        return (sessions.getUserId1() == selfId) ? sessions.getUsername2() : sessions.getUsername1();
    }






}

package com.iot.common.util;

/**
 * Created by zc on 16-8-2.
 */
public class TextUtil {

    public static boolean isEmpty(String str){
        return str==null || str.trim().length()==0;
    }

    public static boolean isEmpty(byte[] b){
        return b==null || b.length==0;
    }
}

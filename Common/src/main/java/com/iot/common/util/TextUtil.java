package com.iot.common.util;

import java.util.Collection;
import java.util.UUID;

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

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static void check(String... strs){
        boolean isEmpty = false;
        if(strs==null || strs.length==0){
            isEmpty = true;
        }else{
            for(String str: strs){
                if(isEmpty(str)){
                    isEmpty = true;
                    break;
                }
            }
        }

        if (isEmpty){
            throw new NullPointerException("param is null");
        }
    }

    public static String uuid(){
        return UUID.randomUUID().toString();
    }
}

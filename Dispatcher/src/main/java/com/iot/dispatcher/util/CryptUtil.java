package com.iot.dispatcher.util;

import java.math.BigInteger;
import java.security.*;

public class CryptUtil {

    public static String md5(String str){
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        }catch (NoSuchAlgorithmException cannotHappen){
            return null;
        }
    }
    
}

package com.iot.tcpserver.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {

    public static byte[] generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keygen.init(128,random);
        return keygen.generateKey().getEncoded();
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = new SecureRandom();
        pairgen.initialize(1024, random);
        return pairgen.generateKeyPair();
    }

    public static byte[] aesEncrypt(byte[] content, byte[] key){
        if(content==null || content.length==0){
            return null;
        }
        if(key==null || key.length==0){
            return null;
        }
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        try {
            return encrypt(content,keySpec,"AES");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] aesDecrypt(byte[] data, byte[] key){
        if(data==null || data.length==0){
            return null;
        }
        if(key==null || key.length==0){
            return null;
        }
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        return decrypt(data,keySpec,"AES");
    }

    public static byte[] rsaDecryptByPrivate(byte[] data, PrivateKey privateKey){
        if(data==null || data.length==0){
            return null;
        }
        if(privateKey==null){
            return null;
        }
        return decrypt(data,privateKey,"RSA");
    }

    public static byte[] rsaEncryptByPublicKey(byte[] data, String rsaPubKey){
        if(data==null || data.length==0){
            return null;
        }
        if(TextUtil.isEmpty(rsaPubKey)){
            return null;
        }
        try {
            return encrypt(data,str2PublicKey(rsaPubKey),"RSA");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] encrypt(byte[] content, Key key, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    private static byte[] decrypt(byte[] content, Key key, String algorithm) {
        if(content==null || content.length==0){
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PublicKey str2PublicKey(String key){
        try{
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static PrivateKey str2PrivateKey(String key){
        try{
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String key2Str(Key key){
        byte[] keyBytes = key.getEncoded();
        return new BASE64Encoder().encode(keyBytes);
    }

    public static String md5(String str){
        if(TextUtil.isEmpty(str)){
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}

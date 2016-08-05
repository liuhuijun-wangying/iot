package com.iot.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {

    public static byte[] generateAESKey(){
        try{
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(128,random);
            return keygen.generateKey().getEncoded();
        }catch (NoSuchAlgorithmException cannotHappen){
            return null;
        }
    }

    public static KeyPair generateKeyPair(){
        try{
            KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            pairgen.initialize(1024, random);
            return pairgen.generateKeyPair();
        }catch (NoSuchAlgorithmException cannotHappen){
            return null;
        }
    }

    public static byte[] aesEncrypt(byte[] content, byte[] key)throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        return encrypt(content,keySpec,"AES");
    }

    public static byte[] aesDecrypt(byte[] data, byte[] key)throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        return decrypt(data,keySpec,"AES");
    }

    public static byte[] rsaDecryptByPrivate(byte[] data, PrivateKey privateKey)throws Exception{
        return decrypt(data,privateKey,"RSA");
    }

    public static byte[] rsaEncryptByPublicKey(byte[] data, String rsaPubKey) throws Exception{
        return encrypt(data,str2PublicKey(rsaPubKey),"RSA");
    }

    private static byte[] encrypt(byte[] content, Key key, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    private static byte[] decrypt(byte[] content, Key key, String algorithm) throws Exception{
        Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    private static PublicKey str2PublicKey(String key) throws Exception {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    private static PrivateKey str2PrivateKey(String key) throws Exception{
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String key2Str(Key key){
        byte[] keyBytes = key.getEncoded();
        return new BASE64Encoder().encode(keyBytes);
    }

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

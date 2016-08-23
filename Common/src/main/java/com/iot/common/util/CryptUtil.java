package com.iot.common.util;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {

    private static final String RSA = "RSA/ECB/PKCS1Padding";
    private static final String AES = "AES/ECB/PKCS5Padding";

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
        return encrypt(content,keySpec,AES);
    }

    public static byte[] aesDecrypt(byte[] data, byte[] key)throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        return decrypt(data,keySpec,AES);
    }

    public static byte[] rsaDecryptByPrivate(byte[] data, PrivateKey privateKey)throws Exception{
        return decrypt(data,privateKey,RSA);
    }

    public static byte[] rsaEncryptByPublicKey(byte[] data, PublicKey pubKey) throws Exception{
        return encrypt(data,pubKey,RSA);
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

    public static PublicKey bytes2PublicKey(byte[] key) throws Exception {
        //byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static PrivateKey bytes2PrivateKey(byte[] key) throws Exception{
        //byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
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

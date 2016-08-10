package com.iot.client;

import com.iot.common.util.CryptUtil;

//用于测试server
//稍加修改，也可用于android
public class ClientMain {

    public static void main(String[] args){
        initAesKey();
        startClient();
    }

    private static void startClient(){
        ClientSocketChannel client = new ClientSocketChannel("127.0.0.1", 8888);
        client.setHandler(new MyHandler());
        //读写idle时间,idle时发心跳包
        //实际可调整为30s
        client.setIdleTimeSecond(60);
        client.start();
    }

    private static void initAesKey(){
        ClientEnv.AES_KEY = CryptUtil.generateAESKey();
    }

}

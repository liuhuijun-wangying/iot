package com.iot.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.client.codec.BaseMsg;
import com.iot.client.utils.HttpUtil;
import com.iot.common.constant.RespCode;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;

//用于测试server
//稍加修改，也可用于android
public class ClientMain {

    //private static final ChannelHandler<ClientSocketChannel,BaseMsg> handler = new DeviceHandler();
    private static final ChannelHandler<ClientSocketChannel,BaseMsg> handler = new AppHandler();

    public static void main(String[] args){
        initAesKey();
        startClient();
    }

    private static void startClient(){
        ClientSocketChannel client = new ClientSocketChannel();
        client.setHandler(handler);
        //读写idle时间,idle时发心跳包
        //实际可调整为30s
        client.setIdleTimeSecond(60);
        client.start();
    }

    private static void initAesKey(){
        ClientEnv.AES_KEY = CryptUtil.generateAESKey();
    }

}

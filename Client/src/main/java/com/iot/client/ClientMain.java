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
        String result = HttpUtil.get("http://127.0.0.1:9999/?id="+ClientEnv.CLIENT_ID);
        if(TextUtil.isEmpty(result)){
            System.err.println("get tcp server addr result is null");
            return;
        }
        JSONObject json = JSON.parseObject(result);
        int statusCode = json.getIntValue("code");
        if(statusCode== RespCode.COMMON_OK){
            ClientSocketChannel client = new ClientSocketChannel(json.getString("ip"),json.getIntValue("port"));
            client.setHandler(handler);
            //读写idle时间,idle时发心跳包
            //实际可调整为30s
            client.setIdleTimeSecond(60);
            client.start();
        }else{
            System.err.println("get tcp server addr err::"+json.getString("msg"));
        }
    }

    private static void initAesKey(){
        ClientEnv.AES_KEY = CryptUtil.generateAESKey();
    }

}

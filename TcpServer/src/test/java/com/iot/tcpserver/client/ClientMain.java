package com.iot.tcpserver.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.tcpserver.channel.ServerEnv;
import com.iot.tcpserver.codec.BaseMsg;
import com.iot.tcpserver.util.CryptUtil;

import java.security.NoSuchAlgorithmException;

//用于测试server
//稍加修改，也可用于android
public class ClientMain {

    public static void main(String[] args) {
        initAesKey();
        startClient();
    }

    private static void startClient(){
        ClientSocketChannel client = new ClientSocketChannel("127.0.0.1", 8080);
        client.setHandler(handler);
        //读写idle时间,idle时发心跳包,生产环境改为30s
        //这里设置为5s方便测试
        client.setIdleTimeSecond(5);
        client.start();
    }

    private static void initAesKey(){
        try {
            ClientEnv.AES_KEY = CryptUtil.generateAESKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("generate aes key failed");
        }
    }

    private static ChannelHandler<ClientSocketChannel,BaseMsg> handler = new ChannelHandler<ClientSocketChannel,BaseMsg>() {

        private final BaseMsg HEARTBEAT_MSG = new BaseMsg(ServerEnv.CMD_HEARTBEAT,null);

        @Override
        public void onRead(ClientSocketChannel ctx, BaseMsg msg) {
            System.out.println("======client recv::::" + msg.toString());
            try{
                switch (msg.getCmd()){
                    case ServerEnv.CMD_PUSH_RSA_PUB_KEY:
                        byte[] b = CryptUtil.rsaEncryptByPublicKey(ClientEnv.AES_KEY,new String(msg.getData(),"UTF-8"));
                        ctx.send(new BaseMsg(ServerEnv.CMD_SEND_AES_KEY,b));
                        break;
                    case ServerEnv.CMD_SEND_AES_KEY://resp
                        if(msg.getData()!=null && msg.getData().length==1){
                            //1代表密钥协商成功,成功后进行认证
                            if(msg.getData()[0]==1){
                                JSONObject json = new JSONObject();
                                json.put("version","1.0");
                                json.put("id","123456789");
                                json.put("username","zc_username");
                                json.put("password",CryptUtil.md5("zc_password"));
                                ctx.send(new BaseMsg(ServerEnv.CMD_APP_AUTH,true,json.toJSONString().getBytes("UTF-8")));
                            }else{
                                //TODO
                            }
                        }
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onIdle(ClientSocketChannel ctx) {//send heartbeat pack
            ctx.send(HEARTBEAT_MSG);
        }

        @Override
        public void onConnected(ClientSocketChannel ctx) {
            System.out.println("----onConnected----");
        }

        @Override
        public void onClosed() {
            System.out.println("----onClosed----");
        }
    };

}

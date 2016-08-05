package com.iot.tcpserver.client;

import com.alibaba.fastjson.JSONObject;
import com.iot.tcpserver.channel.ServerEnv;
import com.iot.tcpserver.codec.BaseMsg;
import com.iot.tcpserver.util.CryptUtil;

//用于测试server
//稍加修改，也可用于android
public class ClientMain {

    public static void main(String[] args){
        initAesKey();
        startClient();
    }

    private static void startClient(){
        ClientSocketChannel client = new ClientSocketChannel("127.0.0.1", 8888);
        client.setHandler(handler);
        //读写idle时间,idle时发心跳包
        client.setIdleTimeSecond(60);
        client.start();
    }

    private static void initAesKey(){
        ClientEnv.AES_KEY = CryptUtil.generateAESKey();
    }

    private static final boolean IS_APP = true;
    private static final BaseMsg HEARTBEAT_MSG = new BaseMsg(ServerEnv.CMD_HEARTBEAT,null);

    private static ChannelHandler<ClientSocketChannel,BaseMsg> handler = new ChannelHandler<ClientSocketChannel,BaseMsg>() {
        //对于一些没有RSA计算能力的设备,不进行密钥协商,直接doAuth
        @Override
        public void onRead(ClientSocketChannel ctx, BaseMsg msg)throws Exception {
            System.out.println("======client recv::::" + msg.toString());
            switch (msg.getCmd()){
                case ServerEnv.CMD_PUSH_RSA_PUB_KEY:
                    if(IS_APP){
                        byte[] b = CryptUtil.rsaEncryptByPublicKey(ClientEnv.AES_KEY,new String(msg.getData(),"UTF-8"));
                        ctx.send(new BaseMsg(ServerEnv.CMD_SEND_AES_KEY,b));
                    }
                    break;
                case ServerEnv.CMD_SEND_AES_KEY://resp
                    if(msg.getData()!=null && msg.getData().length==1 && msg.getData()[0]==1){
                        //1代表密钥协商成功,成功后进行认证
                        doAppAuth(ctx);
                    }else{
                        //TODO handle error
                    }
                    break;
            }
        }

        @Override
        public void onIdle(ClientSocketChannel ctx){//send heartbeat pack
            ctx.send(HEARTBEAT_MSG);
        }

        @Override
        public void onConnected(ClientSocketChannel ctx){
            System.out.println("----onConnected----");
            if(!IS_APP){//device
                try {
                    doDeviceAuth(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClosed(){
            System.out.println("----onClosed----");
        }

        private void doAppAuth(ClientSocketChannel ctx) throws Exception {
            JSONObject json = new JSONObject();
            json.put("version","1.0");
            json.put("id","app123456789");
            json.put("username","zc_username");
            json.put("password",CryptUtil.md5("zc_password"));
            ctx.send(new BaseMsg(ServerEnv.CMD_APP_AUTH,true,json.toJSONString().getBytes("UTF-8")));
        }
        private void doDeviceAuth(ClientSocketChannel ctx) throws Exception {
            JSONObject json = new JSONObject();
            json.put("version","1.0");
            json.put("id","device123456789");
            ctx.send(new BaseMsg(ServerEnv.CMD_APP_AUTH,false,json.toJSONString().getBytes("UTF-8")));
        }
    };

}

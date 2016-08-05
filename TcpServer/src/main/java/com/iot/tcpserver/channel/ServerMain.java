package com.iot.tcpserver.channel;

import com.iot.common.util.CryptUtil;
import com.iot.tcpserver.util.ConfigUtil;

import java.security.KeyPair;

public class ServerMain {

    public static void main(String[] args){
        try{
            initRsaKey();
            initConfig();
            TcpServer.startNetty(ServerEnv.SERVER_PORT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void initRsaKey(){
        KeyPair keyPair = CryptUtil.generateKeyPair();
        ServerEnv.PUBLIC_KEY = CryptUtil.key2Str(keyPair.getPublic());
        ServerEnv.PRIVATE_KEY = keyPair.getPrivate();
    }

    private static void initConfig(){
        String serverPort = ConfigUtil.getProp().getProperty("server_port");
        ServerEnv.SERVER_PORT = Integer.parseInt(serverPort);
    }

}

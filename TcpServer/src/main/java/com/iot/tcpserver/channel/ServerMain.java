package com.iot.tcpserver.channel;

import com.iot.tcpserver.util.ConfigUtil;
import com.iot.tcpserver.util.CryptUtil;

import java.security.KeyPair;

public class ServerMain {

    public static void main(String[] args) {
        initRsaKey();
        initConfig();
        TcpServer.startNetty(ServerEnv.SERVER_PORT);
    }

    private static void initRsaKey(){
        try{
            KeyPair keyPair = CryptUtil.generateKeyPair();
            ServerEnv.PUBLIC_KEY = CryptUtil.key2Str(keyPair.getPublic());
            ServerEnv.PRIVATE_KEY = keyPair.getPrivate();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("generate key failed");
        }
    }

    private static void initConfig(){
        String serverPort = ConfigUtil.getProp().getProperty("server_port","8080");
        ServerEnv.SERVER_PORT = Integer.parseInt(serverPort);
    }

}

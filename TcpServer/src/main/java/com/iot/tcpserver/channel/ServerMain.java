package com.iot.tcpserver.channel;

import com.iot.tcpserver.util.ConfigUtil;
import com.iot.tcpserver.util.CryptUtil;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class ServerMain {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        initRsaKey();
        initConfig();
        TcpServer.startNetty(ServerEnv.SERVER_PORT);
    }

    private static void initRsaKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = CryptUtil.generateKeyPair();
        ServerEnv.PUBLIC_KEY = CryptUtil.key2Str(keyPair.getPublic());
        ServerEnv.PRIVATE_KEY = keyPair.getPrivate();
    }

    private static void initConfig(){
        String serverPort = ConfigUtil.getProp().getProperty("server_port","8080");
        ServerEnv.SERVER_PORT = Integer.parseInt(serverPort);
    }

}
